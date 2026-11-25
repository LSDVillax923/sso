package co.edu.javeriana.sin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Mismas colas y argumentos que declara SSO, para que no choquen al declararlas dos veces. */
@Configuration
public class ConfigRabbitSIN {

    public static final String EXCHANGE = "sso.eventos";
    public static final String SIN_VENTAS = "sin.ventas";
    public static final String SSO_ACK = "sso.ack";

    @Bean
    public Declarables colasSIN() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        Queue ventas = new Queue(SIN_VENTAS, true);
        Queue ack = new Queue(SSO_ACK, true);
        return new Declarables(exchange, ventas, ack,
                BindingBuilder.bind(ventas).to(exchange).with(SIN_VENTAS),
                BindingBuilder.bind(ack).to(exchange).with(SSO_ACK));
    }

    @Bean
    public MessageConverter convertidor(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter convertidor = new Jackson2JsonMessageConverter(objectMapper);
        convertidor.setAlwaysConvertToInferredType(true);
        return convertidor;
    }
}
