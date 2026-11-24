package co.edu.javeriana.sso.notificacion;

import co.edu.javeriana.sso.dominio.mensaje.Colas;
import co.edu.javeriana.sso.dominio.mensaje.MsgNotificacion;
import co.edu.javeriana.sso.dominio.mensaje.MsgVerificarOFAC;
import co.edu.javeriana.sso.dominio.mensaje.TrasCommit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Publica en el exchange sso.eventos: correos, SMS y verificaciones OFAC. Siempre después del commit. */
@Component
public class PublicadorEventos {

    private final RabbitTemplate rabbitTemplate;

    public PublicadorEventos(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarNotificacion(MsgNotificacion mensaje) {
        String cola = "SMS".equals(mensaje.canal()) ? Colas.NOTIF_SMS : Colas.NOTIF_CORREO;
        TrasCommit.ejecutar(() -> rabbitTemplate.convertAndSend(Colas.EXCHANGE, cola, mensaje));
    }

    public void publicarVerificacion(MsgVerificarOFAC mensaje) {
        TrasCommit.ejecutar(() -> rabbitTemplate.convertAndSend(Colas.EXCHANGE, Colas.COMPRAS_OFAC, mensaje));
    }
}
