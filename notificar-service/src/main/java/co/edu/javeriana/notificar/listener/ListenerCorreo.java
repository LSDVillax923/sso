package co.edu.javeriana.notificar.listener;

import co.edu.javeriana.notificar.config.ConfigRabbitNotificar;
import co.edu.javeriana.notificar.dto.DTONotificacion;
import co.edu.javeriana.notificar.srb.SRBEnviarCorreo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Escucha la cola notif.correo del MOM y pasa cada aviso al SRB. */
@Component
public class ListenerCorreo {

    private final SRBEnviarCorreo srbEnviarCorreo;

    public ListenerCorreo(SRBEnviarCorreo srbEnviarCorreo) {
        this.srbEnviarCorreo = srbEnviarCorreo;
    }

    @RabbitListener(queues = ConfigRabbitNotificar.NOTIF_CORREO)
    public void recibir(DTONotificacion dto) {
        srbEnviarCorreo.enviar(dto);
    }
}
