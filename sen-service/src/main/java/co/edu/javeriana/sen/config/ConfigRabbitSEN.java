package co.edu.javeriana.sen.config;

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
public class ConfigRabbitSEN {

    public static final String EXCHANGE = "sso.eventos";
    public static final String DLX = "sso.dlx";
    public static final String SEN_ENVIOS = "sen.envios";
    public static final String SSO_ACK = "sso.ack";

    @Bean
    public Declarables colasSEN() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(DLX, true, false);
        Queue envios = QueueBuilder.durable(SEN_ENVIOS).deadLetterExchange(DLX).deadLetterRoutingKey(SEN_ENVIOS + ".dlq").build();
        Queue enviosDlq = QueueBuilder.durable(SEN_ENVIOS + ".dlq").build();
        Queue ack = QueueBuilder.durable(SSO_ACK).deadLetterExchange(DLX).deadLetterRoutingKey(SSO_ACK + ".dlq").build();
        Queue ackDlq = QueueBuilder.durable(SSO_ACK + ".dlq").build();
        return new Declarables(exchange, dlx, envios, enviosDlq, ack, ackDlq,
                BindingBuilder.bind(envios).to(exchange).with(SEN_ENVIOS),
                BindingBuilder.bind(enviosDlq).to(dlx).with(SEN_ENVIOS + ".dlq"),
                BindingBuilder.bind(ack).to(exchange).with(SSO_ACK),
                BindingBuilder.bind(ackDlq).to(dlx).with(SSO_ACK + ".dlq"));
    }

    @Bean
    public MessageConverter convertidor(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter convertidor = new Jackson2JsonMessageConverter(objectMapper);
        convertidor.setAlwaysConvertToInferredType(true);
        return convertidor;
    }
}
