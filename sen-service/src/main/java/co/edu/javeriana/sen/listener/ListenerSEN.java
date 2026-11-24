package co.edu.javeriana.sen.listener;

import co.edu.javeriana.sen.config.ConfigRabbitSEN;
import co.edu.javeriana.sen.dto.DTOEnvioSEN;
import co.edu.javeriana.sen.srb.SRBProgramarEnvio;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerSEN {

    private final SRBProgramarEnvio srbProgramarEnvio;

    public ListenerSEN(SRBProgramarEnvio srbProgramarEnvio) {
        this.srbProgramarEnvio = srbProgramarEnvio;
    }

    @RabbitListener(queues = ConfigRabbitSEN.SEN_ENVIOS)
    public void recibir(DTOEnvioSEN dto) {
        srbProgramarEnvio.almacenarEnvio(dto);
    }
}
