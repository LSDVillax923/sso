package co.edu.javeriana.sso.dominio.config;

import co.edu.javeriana.sso.dominio.mensaje.Colas;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigRabbit {

    @Bean
    public Declarables colas() {
        return Colas.declarar(Colas.COMPRAS_OFAC, Colas.NOTIF_CORREO, Colas.NOTIF_SMS,
                Colas.SIN_VENTAS, Colas.SEN_ENVIOS, Colas.SSO_ACK);
    }

    @Bean
    public Declarables topico() {
        return Colas.declararTopicoPagos();
    }

    @Bean
    public MessageConverter convertidor(ObjectMapper objectMapper) {
        return Colas.convertidor(objectMapper);
    }
}
