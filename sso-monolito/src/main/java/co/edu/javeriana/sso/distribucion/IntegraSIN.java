package co.edu.javeriana.sso.distribucion;

import co.edu.javeriana.sso.dominio.mensaje.Colas;
import co.edu.javeriana.sso.dominio.mensaje.MsgVentaSIN;
import co.edu.javeriana.sso.dominio.mensaje.TrasCommit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class IntegraSIN {

    private final RabbitTemplate rabbitTemplate;

    public IntegraSIN(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarVenta(MsgVentaSIN mensaje) {
        TrasCommit.ejecutar(() -> rabbitTemplate.convertAndSend(Colas.EXCHANGE, Colas.SIN_VENTAS, mensaje));
    }
}
