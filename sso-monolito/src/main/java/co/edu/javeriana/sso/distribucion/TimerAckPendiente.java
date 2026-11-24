package co.edu.javeriana.sso.distribucion;

import co.edu.javeriana.sso.distribucion.SrvFinalizarCompra;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Si SIN o SEN no confirman a tiempo, se les reenvía la compra (ellos ignoran los repetidos). */
@Component
public class TimerAckPendiente {

    private final SrvFinalizarCompra srvFinalizarCompra;

    public TimerAckPendiente(SrvFinalizarCompra srvFinalizarCompra) {
        this.srvFinalizarCompra = srvFinalizarCompra;
    }

    @Scheduled(fixedDelayString = "${sso.ack.revision-ms:600000}", initialDelay = 60000)
    @SchedulerLock(name = "TimerAckPendiente", lockAtMostFor = "PT9M")
    public void ejecutar() {
        srvFinalizarCompra.revisarAckPendientes();
    }
}
