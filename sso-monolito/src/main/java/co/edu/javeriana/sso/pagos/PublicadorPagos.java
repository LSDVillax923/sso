package co.edu.javeriana.sso.pagos;

import co.edu.javeriana.sso.dominio.mensaje.Colas;
import co.edu.javeriana.sso.dominio.mensaje.TrasCommit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Lado Java del tópico integracion.pagos: lo que el monolito SSO le manda al monolito .NET de YoPago. */
@Component
public class PublicadorPagos {

    private final RabbitTemplate rabbitTemplate;

    public PublicadorPagos(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /** Cédula del cliente, número de compra y valor: YoPago la muestra como compra SSO pendiente. */
    public void publicarPendiente(CompraPendienteDTO dto) {
        TrasCommit.ejecutar(() -> rabbitTemplate.convertAndSend(Colas.TOPICO_PAGOS, Colas.CLAVE_PAGO_PENDIENTE, dto));
    }

    /** Si la compra ya se pagó, YoPago vuelve a publicar pago.confirmado. */
    public void publicarConsulta(Long numeroCompra) {
        TrasCommit.ejecutar(() -> rabbitTemplate.convertAndSend(Colas.TOPICO_PAGOS, Colas.CLAVE_PAGO_CONSULTA,
                new ConsultaPagoDTO(numeroCompra)));
    }
}
