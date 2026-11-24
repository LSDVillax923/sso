package co.edu.javeriana.sin.config;

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
public class ConfigRabbitSIN {

    public static final String EXCHANGE = "sso.eventos";
    public static final String DLX = "sso.dlx";
    public static final String SIN_VENTAS = "sin.ventas";
    public static final String SSO_ACK = "sso.ack";

    @Bean
    public Declarables colasSIN() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(DLX, true, false);
        Queue ventas = QueueBuilder.durable(SIN_VENTAS).deadLetterExchange(DLX).deadLetterRoutingKey(SIN_VENTAS + ".dlq").build();
        Queue ventasDlq = QueueBuilder.durable(SIN_VENTAS + ".dlq").build();
        Queue ack = QueueBuilder.durable(SSO_ACK).deadLetterExchange(DLX).deadLetterRoutingKey(SSO_ACK + ".dlq").build();
        Queue ackDlq = QueueBuilder.durable(SSO_ACK + ".dlq").build();
        return new Declarables(exchange, dlx, ventas, ventasDlq, ack, ackDlq,
                BindingBuilder.bind(ventas).to(exchange).with(SIN_VENTAS),
                BindingBuilder.bind(ventasDlq).to(dlx).with(SIN_VENTAS + ".dlq"),
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
