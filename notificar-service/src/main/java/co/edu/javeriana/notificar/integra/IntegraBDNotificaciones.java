package co.edu.javeriana.notificar.integra;

import co.edu.javeriana.notificar.entidad.EstadoNotificacion;
import co.edu.javeriana.notificar.entidad.Notificacion;
import co.edu.javeriana.notificar.repo.RepoNotificacion;
import org.springframework.stereotype.Component;

/** Única salida de notificar-service hacia bd_notificaciones (JDBC 5432): registra cada aviso y su resultado. */
@Component
public class IntegraBDNotificaciones {

    private final RepoNotificacion repoNotificacion;

    public IntegraBDNotificaciones(RepoNotificacion repoNotificacion) {
        this.repoNotificacion = repoNotificacion;
    }

    /** Inserta el aviso como PENDIENTE y devuelve su id. */
    public Long registrar(String canal, String destino, String plantilla, String texto) {
        return repoNotificacion.save(new Notificacion(canal, destino, plantilla, texto)).getIdNotificacion();
    }

    public void marcar(Long idNotificacion, EstadoNotificacion estado) {
        repoNotificacion.findById(idNotificacion).ifPresent(notificacion -> {
            notificacion.marcar(estado);
            repoNotificacion.save(notificacion);
        });
    }
}
