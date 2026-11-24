package co.edu.javeriana.notificar.repo;

import co.edu.javeriana.notificar.entidad.EstadoNotificacion;
import co.edu.javeriana.notificar.entidad.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoNotificacion extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByEstado(EstadoNotificacion estado);
}
