package co.edu.javeriana.notificar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Mismas colas y argumentos que declara SSO, para que no choquen al declararlas dos veces. */
@Configuration
public class ConfigRabbitNotificar {

    public static final String EXCHANGE = "sso.eventos";
    public static final String DLX = "sso.dlx";
    public static final String NOTIF_CORREO = "notif.correo";
    public static final String NOTIF_SMS = "notif.sms";

    @Bean
    public Declarables colasNotificar() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(DLX, true, false);
        Queue correo = QueueBuilder.durable(NOTIF_CORREO).deadLetterExchange(DLX).deadLetterRoutingKey(NOTIF_CORREO + ".dlq").build();
        Queue correoDlq = QueueBuilder.durable(NOTIF_CORREO + ".dlq").build();
        Queue sms = QueueBuilder.durable(NOTIF_SMS).deadLetterExchange(DLX).deadLetterRoutingKey(NOTIF_SMS + ".dlq").build();
        Queue smsDlq = QueueBuilder.durable(NOTIF_SMS + ".dlq").build();
        return new Declarables(exchange, dlx, correo, correoDlq, sms, smsDlq,
                BindingBuilder.bind(correo).to(exchange).with(NOTIF_CORREO),
                BindingBuilder.bind(correoDlq).to(dlx).with(NOTIF_CORREO + ".dlq"),
                BindingBuilder.bind(sms).to(exchange).with(NOTIF_SMS),
                BindingBuilder.bind(smsDlq).to(dlx).with(NOTIF_SMS + ".dlq"));
    }

    @Bean
    public MessageConverter convertidor(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter convertidor = new Jackson2JsonMessageConverter(objectMapper);
        convertidor.setAlwaysConvertToInferredType(true);
        return convertidor;
    }
}
