package co.edu.javeriana.sin.integra;

import co.edu.javeriana.sin.config.ConfigRabbitSIN;
import co.edu.javeriana.sin.dto.DTOAckSIN;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Salida de SIN hacia el MOM (AMQP 5672, cola sso.ack): confirma a SSO que SIN recibió y guardó la venta. Se envía después del commit. */
@Component
public class IntegraAckSIN {

    private final RabbitTemplate rabbitTemplate;

    public IntegraAckSIN(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarAck(Long numeroCompra) {
        DTOAckSIN ack = new DTOAckSIN(numeroCompra, "SIN");
        Runnable envio = () -> rabbitTemplate.convertAndSend(ConfigRabbitSIN.EXCHANGE, ConfigRabbitSIN.SSO_ACK, ack);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    envio.run();
                }
            });
        } else {
            envio.run();
        }
    }
}
