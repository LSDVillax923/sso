package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.integra.IntegraBDCompra;
import co.edu.javeriana.sso.dominio.mensaje.MsgVerificarOFAC;
import co.edu.javeriana.sso.notificacion.PublicadorEventos;
import java.time.LocalDateTime;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Reintenta OFAC cuando respondió ENPROCESO o hubo caída de red. Corre en una sola réplica. */
@Component
public class TimerReintentoOFAC {

    private final IntegraBDCompra integraBDCompra;
    private final PublicadorEventos publicadorEventos;

    public TimerReintentoOFAC(IntegraBDCompra integraBDCompra, PublicadorEventos publicadorEventos) {
        this.integraBDCompra = integraBDCompra;
        this.publicadorEventos = publicadorEventos;
    }

    @Scheduled(fixedDelayString = "${sso.ofac.revision-ms:300000}", initialDelay = 60000)
    @SchedulerLock(name = "TimerReintentoOFAC", lockAtMostFor = "PT4M")
    public void ejecutar() {
        integraBDCompra.findByEstadoAndProximoIntentoBefore(EstadoCompra.VERIFICANDO_OFAC, LocalDateTime.now())
                .forEach(c -> publicadorEventos.publicarVerificacion(
                        new MsgVerificarOFAC(c.getNumeroCompra(), c.getCliente().getCedula())));
    }
}
