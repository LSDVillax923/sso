package co.edu.javeriana.sso.pagos;

import co.edu.javeriana.sso.pagos.SrvVerificarPago;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Por si se pierde el aviso de YoPago: consulta las compras que siguen PENDIENTE_PAGO. */
@Component
public class TimerVerificarPago {

    private final SrvVerificarPago srvVerificarPago;

    public TimerVerificarPago(SrvVerificarPago srvVerificarPago) {
        this.srvVerificarPago = srvVerificarPago;
    }

    @Scheduled(fixedDelayString = "${sso.pagos.revision-ms:600000}", initialDelay = 60000)
    @SchedulerLock(name = "TimerVerificarPago", lockAtMostFor = "PT9M")
    public void ejecutar() {
        srvVerificarPago.verificarPendientes();
    }
}
