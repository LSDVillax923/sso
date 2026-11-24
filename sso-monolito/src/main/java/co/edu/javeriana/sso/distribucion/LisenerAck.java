package co.edu.javeriana.sso.distribucion;

import co.edu.javeriana.sso.distribucion.SrvFinalizarCompra;
import co.edu.javeriana.sso.dominio.mensaje.Colas;
import co.edu.javeriana.sso.dominio.mensaje.MsgAck;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LisenerAck {

    private final SrvFinalizarCompra srvFinalizarCompra;

    public LisenerAck(SrvFinalizarCompra srvFinalizarCompra) {
        this.srvFinalizarCompra = srvFinalizarCompra;
    }

    @RabbitListener(queues = Colas.SSO_ACK)
    public void recibir(MsgAck ack) {
        srvFinalizarCompra.registrarAck(ack);
    }
}
