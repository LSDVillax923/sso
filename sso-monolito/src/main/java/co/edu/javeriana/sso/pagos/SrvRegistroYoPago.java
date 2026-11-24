package co.edu.javeriana.sso.pagos;

import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.integra.IntegraBDCompra;
import co.edu.javeriana.sso.dominio.mensaje.MsgNotificacion;
import co.edu.javeriana.sso.dominio.mensaje.PlantillaMensaje;
import co.edu.javeriana.sso.notificacion.PublicadorEventos;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Publica en el tópico la compra pendiente para YoPago y avisa al celular el valor y la URL de pago. */
@Service
public class SrvRegistroYoPago {

    private final PublicadorPagos publicadorPagos;
    private final IntegraBDCompra integraBDCompra;
    private final PublicadorEventos publicadorEventos;
    private final String urlPagoYoPago;

    public SrvRegistroYoPago(PublicadorPagos publicadorPagos, IntegraBDCompra integraBDCompra, PublicadorEventos publicadorEventos,
                             @Value("${sso.yopago.url-pago}") String urlPagoYoPago) {
        this.publicadorPagos = publicadorPagos;
        this.integraBDCompra = integraBDCompra;
        this.publicadorEventos = publicadorEventos;
        this.urlPagoYoPago = urlPagoYoPago;
    }

    /** Si YoPago está caído el mensaje espera en la cola yopago.pendientes; no hay que reintentar desde aquí. */
    @Transactional
    public void registrarCompraPendiente(Compra compra) {
        publicadorPagos.publicarPendiente(
                new CompraPendienteDTO(compra.getCliente().getCedula(), compra.getNumeroCompra(), compra.getValorTotal()));
        compra.setEstado(EstadoCompra.PENDIENTE_PAGO);
        integraBDCompra.save(compra);
        publicadorEventos.publicarNotificacion(MsgNotificacion.sms(compra.getCliente().getCelular(), PlantillaMensaje.OFAC_APROBADO,
                Map.of("numeroCompra", String.valueOf(compra.getNumeroCompra()),
                        "valor", compra.getValorTotal().toPlainString(),
                        "url", urlPagoYoPago)));
    }
}
