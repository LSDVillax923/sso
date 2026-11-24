package co.edu.javeriana.sso.distribucion;

import co.edu.javeriana.sso.dominio.mensaje.Colas;
import co.edu.javeriana.sso.dominio.mensaje.MsgEnvioSEN;
import co.edu.javeriana.sso.dominio.mensaje.TrasCommit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class IntegraSEN {

    private final RabbitTemplate rabbitTemplate;

    public IntegraSEN(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarEnvio(MsgEnvioSEN mensaje) {
        TrasCommit.ejecutar(() -> rabbitTemplate.convertAndSend(Colas.EXCHANGE, Colas.SEN_ENVIOS, mensaje));
    }
}
