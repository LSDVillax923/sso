package co.edu.javeriana.sso.pagos;

import co.edu.javeriana.sso.dominio.mensaje.Colas;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Recibe de YoPago (.NET) cédula, número de compra y valor pagado. Las 2 réplicas compiten por la cola. */
@Component
public class LisenerPagoConfirmado {

    private final SrvVerificarPago srvVerificarPago;

    public LisenerPagoConfirmado(SrvVerificarPago srvVerificarPago) {
        this.srvVerificarPago = srvVerificarPago;
    }

    @RabbitListener(queues = Colas.SSO_PAGOS_CONFIRMADOS)
    public void recibir(ConfirmacionPagoDTO dto) {
        srvVerificarPago.confirmarPago(dto);
    }
}
