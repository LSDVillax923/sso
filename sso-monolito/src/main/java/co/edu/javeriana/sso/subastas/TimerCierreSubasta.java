package co.edu.javeriana.sso.subastas;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Abre las subastas que llegaron a su fecha de inicio y cierra las vencidas. Corre en una sola réplica. */
@Component
public class TimerCierreSubasta {

    private final SrvCierreSubasta srvCierreSubasta;

    public TimerCierreSubasta(SrvCierreSubasta srvCierreSubasta) {
        this.srvCierreSubasta = srvCierreSubasta;
    }

    @Scheduled(fixedRate = 60000)
    @SchedulerLock(name = "TimerCierreSubasta", lockAtMostFor = "PT50S")
    public void ejecutar() {
        srvCierreSubasta.abrirSubastasProgramadas();
        srvCierreSubasta.cerrarSubastasVencidas();
    }
}
