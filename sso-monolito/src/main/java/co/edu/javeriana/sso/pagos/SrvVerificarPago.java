package co.edu.javeriana.sso.pagos;

import co.edu.javeriana.sso.distribucion.SrvDistribuirCompra;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.entidad.Pago;
import co.edu.javeriana.sso.dominio.repo.RepoCompra;
import co.edu.javeriana.sso.dominio.repo.RepoPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** SSO revisa si el cliente ya pagó: con la confirmación que llega por el tópico o preguntando a YoPago. */
@Service
public class SrvVerificarPago {

    private static final Logger log = LoggerFactory.getLogger(SrvVerificarPago.class);

    private final RepoCompra repoCompra;
    private final RepoPago repoPago;
    private final PublicadorPagos publicadorPagos;
    private final SrvDistribuirCompra srvDistribuirCompra;
    private final int esperaMin;

    public SrvVerificarPago(RepoCompra repoCompra, RepoPago repoPago, PublicadorPagos publicadorPagos,
                            SrvDistribuirCompra srvDistribuirCompra,
                            @Value("${sso.pagos.espera-min:10}") int esperaMin) {
        this.repoCompra = repoCompra;
        this.repoPago = repoPago;
        this.publicadorPagos = publicadorPagos;
        this.srvDistribuirCompra = srvDistribuirCompra;
        this.esperaMin = esperaMin;
    }

    @Transactional
    public void confirmarPago(ConfirmacionPagoDTO dto) {
        registrarPago(dto);
    }

    /** Compras que siguen sin pago después de un rato: se le pregunta a YoPago por el tópico. */
    @Transactional(readOnly = true)
    public void verificarPendientes() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(esperaMin);
        repoCompra.findByEstadoAndFechaActualizacionBefore(EstadoCompra.PENDIENTE_PAGO, limite)
                .forEach(c -> publicadorPagos.publicarConsulta(c.getNumeroCompra()));
    }

    private void registrarPago(ConfirmacionPagoDTO dto) {
        Compra compra = repoCompra.bloquear(dto.numeroCompra()).orElse(null);
        if (compra == null) {
            log.warn("La compra {} no existe para la cédula {}", dto.numeroCompra(), dto.cedula());
            return;
        }
        if (!compra.getCliente().getCedula().equals(dto.cedula())) {
            log.warn("La cédula {} no corresponde a la compra {}", dto.cedula(), dto.numeroCompra());
            return;
        }
        if (compra.getEstado() != EstadoCompra.PENDIENTE_PAGO) {
            return; // confirmación repetida: la operación es idempotente
        }
        if (!esPagoValido(compra, dto.valorPagado())) {
            log.warn("Pago insuficiente para la compra {}. Valor pagado {}. Total {}",
                    compra.getNumeroCompra(), dto.valorPagado(), compra.getValorTotal());
            return;
        }
        Pago pago = new Pago();
        pago.setCompra(compra);
        pago.setValorPagado(dto.valorPagado());
        pago.setFechaPago(LocalDateTime.now());
        repoPago.save(pago);
        srvDistribuirCompra.distribuir(compra);
    }

    private boolean esPagoValido(Compra compra, BigDecimal valorPagado) {
        return valorPagado != null && valorPagado.compareTo(compra.getValorTotal()) >= 0;
    }
}
