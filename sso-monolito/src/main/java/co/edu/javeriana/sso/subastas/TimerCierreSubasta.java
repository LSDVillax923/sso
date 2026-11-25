package co.edu.javeriana.sso.subastas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Abre las subastas que llegaron a su fecha de inicio y cierra las vencidas. Corre en una sola réplica. */
@Component
public class TimerCierreSubasta {

    private final SrvCierreSubasta srvCierreSubasta;
    private final boolean activos;

    public TimerCierreSubasta(SrvCierreSubasta srvCierreSubasta,
                              @Value("${sso.timers.activos}") boolean activos) {
        this.activos = activos;
        this.srvCierreSubasta = srvCierreSubasta;
    }

    @Scheduled(fixedRate = 60000)
    public void ejecutar() {
        if (!activos) return; // C9 s8: los timers solo corren en la réplica 1
        srvCierreSubasta.abrirSubastasProgramadas();
        srvCierreSubasta.cerrarSubastasVencidas();
    }
}
