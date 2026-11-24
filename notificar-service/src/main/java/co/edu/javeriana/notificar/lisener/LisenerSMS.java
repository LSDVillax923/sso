package co.edu.javeriana.notificar.lisener;

import co.edu.javeriana.notificar.config.ConfigRabbitNotificar;
import co.edu.javeriana.notificar.dto.DTONotificacion;
import co.edu.javeriana.notificar.srb.SRBEnviarSMS;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Escucha la cola notif.sms del MOM y pasa cada aviso al SRB. */
@Component
public class LisenerSMS {

    private final SRBEnviarSMS srbEnviarSMS;

    public LisenerSMS(SRBEnviarSMS srbEnviarSMS) {
        this.srbEnviarSMS = srbEnviarSMS;
    }

    @RabbitListener(queues = ConfigRabbitNotificar.NOTIF_SMS)
    public void recibir(DTONotificacion dto) {
        srbEnviarSMS.enviar(dto);
    }
}
