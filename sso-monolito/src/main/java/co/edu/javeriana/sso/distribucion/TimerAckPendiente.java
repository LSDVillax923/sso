package co.edu.javeriana.sso.distribucion;

import co.edu.javeriana.sso.distribucion.SrvFinalizarCompra;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Si SIN o SEN no confirman a tiempo, se les reenvía la compra (ellos ignoran los repetidos). */
@Component
public class TimerAckPendiente {

    private final SrvFinalizarCompra srvFinalizarCompra;
    private final boolean activos;

    public TimerAckPendiente(SrvFinalizarCompra srvFinalizarCompra,
                             @Value("${sso.timers.activos}") boolean activos) {
        this.activos = activos;
        this.srvFinalizarCompra = srvFinalizarCompra;
    }

    @Scheduled(fixedDelayString = "${sso.ack.revision-ms:600000}", initialDelay = 60000)
    public void ejecutar() {
        if (!activos) return; // C9 s8: los timers solo corren en la réplica 1
        srvFinalizarCompra.revisarAckPendientes();
    }
}
