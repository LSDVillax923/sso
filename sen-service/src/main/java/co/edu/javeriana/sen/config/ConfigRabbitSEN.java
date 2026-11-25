package co.edu.javeriana.sen.config;

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
public class ConfigRabbitSEN {

    public static final String EXCHANGE = "sso.eventos";
    public static final String SEN_ENVIOS = "sen.envios";
    public static final String SSO_ACK = "sso.ack";

    @Bean
    public Declarables colasSEN() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        Queue envios = new Queue(SEN_ENVIOS, true);
        Queue ack = new Queue(SSO_ACK, true);
        return new Declarables(exchange, envios, ack,
                BindingBuilder.bind(envios).to(exchange).with(SEN_ENVIOS),
                BindingBuilder.bind(ack).to(exchange).with(SSO_ACK));
    }

    @Bean
    public MessageConverter convertidor(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter convertidor = new Jackson2JsonMessageConverter(objectMapper);
        convertidor.setAlwaysConvertToInferredType(true);
        return convertidor;
    }
}
