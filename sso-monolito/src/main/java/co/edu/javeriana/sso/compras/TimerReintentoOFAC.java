package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.repo.RepoCompra;
import co.edu.javeriana.sso.dominio.mensaje.MsgVerificarOFAC;
import co.edu.javeriana.sso.notificacion.PublicadorEventos;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Reintenta OFAC cuando respondió ENPROCESO o hubo caída de red. Corre en una sola réplica. */
@Component
public class TimerReintentoOFAC {

    private final RepoCompra repoCompra;
    private final PublicadorEventos publicadorEventos;
    private final boolean activos;

    public TimerReintentoOFAC(RepoCompra repoCompra, PublicadorEventos publicadorEventos,
                              @Value("${sso.timers.activos}") boolean activos) {
        this.activos = activos;
        this.repoCompra = repoCompra;
        this.publicadorEventos = publicadorEventos;
    }

    @Scheduled(fixedDelayString = "${sso.ofac.revision-ms:300000}", initialDelay = 60000)
    public void ejecutar() {
        if (!activos) return; // C9 s8: los timers solo corren en la réplica 1
        repoCompra.findByEstadoAndProximoIntentoBefore(EstadoCompra.VERIFICANDO_OFAC, LocalDateTime.now())
                .forEach(c -> publicadorEventos.publicarVerificacion(
                        new MsgVerificarOFAC(c.getNumeroCompra(), c.getCliente().getCedula())));
    }
}
