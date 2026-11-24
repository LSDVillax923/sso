package co.edu.javeriana.sen.integra;

import co.edu.javeriana.sen.config.ConfigRabbitSEN;
import co.edu.javeriana.sen.dto.DTOAckSEN;
import java.time.LocalDateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Salida de SEN hacia el MOM (AMQP 5672, cola sso.ack): confirma a SSO que SEN recibió y guardó el envío. Se envía después del commit. */
@Component
public class IntegraAckSEN {

    private final RabbitTemplate rabbitTemplate;

    public IntegraAckSEN(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarAck(Long numeroCompra) {
        DTOAckSEN ack = new DTOAckSEN(numeroCompra, "SEN", LocalDateTime.now());
        Runnable envio = () -> rabbitTemplate.convertAndSend(ConfigRabbitSEN.EXCHANGE, ConfigRabbitSEN.SSO_ACK, ack);
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
