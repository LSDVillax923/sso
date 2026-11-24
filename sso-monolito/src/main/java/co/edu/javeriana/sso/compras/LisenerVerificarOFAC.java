package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.compras.SrvVerificarOFAC;
import co.edu.javeriana.sso.dominio.mensaje.Colas;
import co.edu.javeriana.sso.dominio.mensaje.MsgVerificarOFAC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Las dos réplicas escuchan la misma cola: RabbitMQ reparte los mensajes entre ellas. */
@Component
public class LisenerVerificarOFAC {

    private final SrvVerificarOFAC srvVerificarOFAC;
    private final IntegraOFAC integraOFAC;

    public LisenerVerificarOFAC(SrvVerificarOFAC srvVerificarOFAC, IntegraOFAC integraOFAC) {
        this.srvVerificarOFAC = srvVerificarOFAC;
        this.integraOFAC = integraOFAC;
    }

    @RabbitListener(queues = Colas.COMPRAS_OFAC)
    public void recibir(MsgVerificarOFAC mensaje) {
        RespuestaOFACDTO respuesta = integraOFAC.consultar(mensaje.cedula());
        srvVerificarOFAC.verificar(mensaje.numeroCompra(), respuesta);
    }
}
