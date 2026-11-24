package co.edu.javeriana.sso.dominio.mensaje;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * Nombres de RabbitMQ. Exchange sso.eventos (direct): colas internas del SSO, cada una con su .dlq.
 * Tópico integracion.pagos: une el monolito Spring Boot con el monolito .NET de YoPago.
 */
public final class Colas {

    public static final String EXCHANGE = "sso.eventos";
    public static final String DLX = "sso.dlx";
    public static final String COMPRAS_OFAC = "compras.ofac";
    public static final String NOTIF_CORREO = "notif.correo";
    public static final String NOTIF_SMS = "notif.sms";
    public static final String SIN_VENTAS = "sin.ventas";
    public static final String SEN_ENVIOS = "sen.envios";
    public static final String SSO_ACK = "sso.ack";

    public static final String TOPICO_PAGOS = "integracion.pagos";
    public static final String CLAVE_PAGO_PENDIENTE = "pago.pendiente";
    public static final String CLAVE_PAGO_CONSULTA = "pago.consulta";
    public static final String CLAVE_PAGO_CONFIRMADO = "pago.confirmado";
    public static final String YOPAGO_PENDIENTES = "yopago.pendientes";
    public static final String YOPAGO_CONSULTAS = "yopago.consultas";
    public static final String SSO_PAGOS_CONFIRMADOS = "sso.pagos.confirmados";

    private Colas() {
    }

    /** Declarar la misma cola desde varios servicios no da error mientras los argumentos sean iguales. */
    public static Declarables declarar(String... colas) {
        List<Declarable> d = new ArrayList<>();
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(DLX, true, false);
        d.add(exchange);
        d.add(dlx);
        for (String nombre : colas) {
            Queue cola = QueueBuilder.durable(nombre).deadLetterExchange(DLX).deadLetterRoutingKey(nombre + ".dlq").build();
            Queue dlq = QueueBuilder.durable(nombre + ".dlq").build();
            d.add(cola);
            d.add(dlq);
            d.add(BindingBuilder.bind(cola).to(exchange).with(nombre));
            d.add(BindingBuilder.bind(dlq).to(dlx).with(nombre + ".dlq"));
        }
        return new Declarables(d);
    }

    /** Mismos nombres y argumentos que declara YoPago (ConfigRabbitYP): colas durables sin argumentos. */
    public static Declarables declararTopicoPagos() {
        TopicExchange topico = new TopicExchange(TOPICO_PAGOS, true, false);
        Queue pendientes = QueueBuilder.durable(YOPAGO_PENDIENTES).build();
        Queue consultas = QueueBuilder.durable(YOPAGO_CONSULTAS).build();
        Queue confirmados = QueueBuilder.durable(SSO_PAGOS_CONFIRMADOS).build();
        return new Declarables(topico, pendientes, consultas, confirmados,
                BindingBuilder.bind(pendientes).to(topico).with(CLAVE_PAGO_PENDIENTE),
                BindingBuilder.bind(consultas).to(topico).with(CLAVE_PAGO_CONSULTA),
                BindingBuilder.bind(confirmados).to(topico).with(CLAVE_PAGO_CONFIRMADO));
    }

    /** JSON entre sistemas: el consumidor convierte al tipo de su parámetro, no al nombre de clase del productor. */
    public static MessageConverter convertidor(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter convertidor = new Jackson2JsonMessageConverter(objectMapper);
        convertidor.setAlwaysConvertToInferredType(true);
        return convertidor;
    }
}
