package co.edu.javeriana.sso.pagos;

import co.edu.javeriana.sso.distribucion.SrvDistribuirCompra;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.entidad.Pago;
import co.edu.javeriana.sso.dominio.error.ErrorNegocio;
import co.edu.javeriana.sso.dominio.integra.IntegraBDCompra;
import co.edu.javeriana.sso.dominio.integra.IntegraBDPago;
import co.edu.javeriana.sso.dominio.mensaje.MsgNotificacion;
import co.edu.javeriana.sso.dominio.mensaje.PlantillaMensaje;
import co.edu.javeriana.sso.notificacion.PublicadorEventos;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** SSO revisa si el cliente ya pagó: con la confirmación que llega por el tópico o preguntando a YoPago. */
@Service
public class SrvVerificarPago {

    private final IntegraBDCompra integraBDCompra;
    private final IntegraBDPago integraBDPago;
    private final PublicadorPagos publicadorPagos;
    private final SrvDistribuirCompra srvDistribuirCompra;
    private final PublicadorEventos publicadorEventos;
    private final int esperaMin;

    public SrvVerificarPago(IntegraBDCompra integraBDCompra, IntegraBDPago integraBDPago, PublicadorPagos publicadorPagos,
                            SrvDistribuirCompra srvDistribuirCompra, PublicadorEventos publicadorEventos,
                            @Value("${sso.pagos.espera-min:10}") int esperaMin) {
        this.integraBDCompra = integraBDCompra;
        this.integraBDPago = integraBDPago;
        this.publicadorPagos = publicadorPagos;
        this.srvDistribuirCompra = srvDistribuirCompra;
        this.publicadorEventos = publicadorEventos;
        this.esperaMin = esperaMin;
    }

    @Transactional
    public void confirmarPago(ConfirmacionPagoDTO dto) {
        registrarPago(dto, "TOPICO_YOPAGO");
    }

    /** Compras que siguen sin pago después de un rato: se le pregunta a YoPago por el tópico. */
    @Transactional(readOnly = true)
    public void verificarPendientes() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(esperaMin);
        integraBDCompra.findByEstadoAndFechaActualizacionBefore(EstadoCompra.PENDIENTE_PAGO, limite)
                .forEach(c -> publicadorPagos.publicarConsulta(c.getNumeroCompra()));
    }

    private void registrarPago(ConfirmacionPagoDTO dto, String origen) {
        Compra compra = integraBDCompra.bloquear(dto.numeroCompra())
                .orElseThrow(() -> new ErrorNegocio(HttpStatus.NOT_FOUND, "La compra no existe"));
        if (!compra.getCliente().getCedula().equals(dto.cedula())) {
            throw new ErrorNegocio(HttpStatus.BAD_REQUEST, "La cédula no corresponde a la compra");
        }
        if (compra.getEstado() != EstadoCompra.PENDIENTE_PAGO && compra.getEstado() != EstadoCompra.PAGO_INCOMPLETO) {
            return; // confirmación repetida: la operación es idempotente
        }
        if (!esPagoValido(compra, dto.valorPagado())) {
            compra.setEstado(EstadoCompra.PAGO_INCOMPLETO);
            integraBDCompra.save(compra);
            publicadorEventos.publicarNotificacion(MsgNotificacion.sms(compra.getCliente().getCelular(), PlantillaMensaje.PAGO_INCOMPLETO,
                    Map.of("numeroCompra", String.valueOf(compra.getNumeroCompra()),
                            "valorPagado", String.valueOf(dto.valorPagado()),
                            "valorTotal", compra.getValorTotal().toPlainString())));
            return;
        }
        Pago pago = new Pago();
        pago.setCompra(compra);
        pago.setValorPagado(dto.valorPagado());
        pago.setFechaPago(LocalDateTime.now());
        pago.setOrigen(origen);
        integraBDPago.save(pago);
        compra.setEstado(EstadoCompra.PAGADA);
        integraBDCompra.save(compra);
        srvDistribuirCompra.distribuir(compra);
    }

    private boolean esPagoValido(Compra compra, BigDecimal valorPagado) {
        return valorPagado != null && valorPagado.compareTo(compra.getValorTotal()) >= 0;
    }
}
