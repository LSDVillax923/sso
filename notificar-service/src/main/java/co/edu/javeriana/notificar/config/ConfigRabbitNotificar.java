package co.edu.javeriana.notificar.config;

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
public class ConfigRabbitNotificar {

    public static final String EXCHANGE = "sso.eventos";
    public static final String NOTIF_CORREO = "notif.correo";
    public static final String NOTIF_SMS = "notif.sms";

    @Bean
    public Declarables colasNotificar() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        Queue correo = new Queue(NOTIF_CORREO, true);
        Queue sms = new Queue(NOTIF_SMS, true);
        return new Declarables(exchange, correo, sms,
                BindingBuilder.bind(correo).to(exchange).with(NOTIF_CORREO),
                BindingBuilder.bind(sms).to(exchange).with(NOTIF_SMS));
    }

    @Bean
    public MessageConverter convertidor(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter convertidor = new Jackson2JsonMessageConverter(objectMapper);
        convertidor.setAlwaysConvertToInferredType(true);
        return convertidor;
    }
}
