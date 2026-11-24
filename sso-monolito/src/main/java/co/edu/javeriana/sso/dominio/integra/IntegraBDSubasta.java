package co.edu.javeriana.sso.dominio.integra;

import co.edu.javeriana.sso.dominio.entidad.EstadoSubasta;
import co.edu.javeriana.sso.dominio.entidad.Subasta;
import co.edu.javeriana.sso.dominio.repo.RepoSubasta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Salida del monolito hacia bd_sso (JDBC 5432) para las subastas y sus bienes. Los Srv no tocan el repositorio. */
@Component
public class IntegraBDSubasta {

    private final RepoSubasta repoSubasta;

    public IntegraBDSubasta(RepoSubasta repoSubasta) {
        this.repoSubasta = repoSubasta;
    }

    public Optional<Subasta> findById(Long idSubasta) {
        return repoSubasta.findById(idSubasta);
    }

    public List<Subasta> buscarAbiertas(String filtro, LocalDateTime ahora) {
        return repoSubasta.buscarAbiertas(filtro, ahora);
    }

    public List<Subasta> findByEstadoAndFechaCierreBefore(EstadoSubasta estado, LocalDateTime fecha) {
        return repoSubasta.findByEstadoAndFechaCierreBefore(estado, fecha);
    }

    public List<Subasta> findByEstadoAndFechaInicioBefore(EstadoSubasta estado, LocalDateTime fecha) {
        return repoSubasta.findByEstadoAndFechaInicioBefore(estado, fecha);
    }
}
