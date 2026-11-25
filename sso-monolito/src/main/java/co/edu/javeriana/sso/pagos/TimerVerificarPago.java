package co.edu.javeriana.sso.pagos;

import co.edu.javeriana.sso.pagos.SrvVerificarPago;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Por si se pierde el aviso de YoPago: consulta las compras que siguen PENDIENTE_PAGO. */
@Component
public class TimerVerificarPago {

    private final SrvVerificarPago srvVerificarPago;
    private final boolean activos;

    public TimerVerificarPago(SrvVerificarPago srvVerificarPago,
                              @Value("${sso.timers.activos}") boolean activos) {
        this.activos = activos;
        this.srvVerificarPago = srvVerificarPago;
    }

    @Scheduled(fixedDelayString = "${sso.pagos.revision-ms:600000}", initialDelay = 60000)
    public void ejecutar() {
        if (!activos) return; // C9 s8: los timers solo corren en la réplica 1
        srvVerificarPago.verificarPendientes();
    }
}
