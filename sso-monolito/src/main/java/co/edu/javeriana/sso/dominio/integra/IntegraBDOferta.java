package co.edu.javeriana.sso.dominio.integra;

import co.edu.javeriana.sso.dominio.entidad.EstadoOferta;
import co.edu.javeriana.sso.dominio.entidad.Oferta;
import co.edu.javeriana.sso.dominio.entidad.Subasta;
import co.edu.javeriana.sso.dominio.repo.RepoOferta;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Salida del monolito hacia bd_sso (JDBC 5432) para las ofertas. Los Srv no tocan el repositorio. */
@Component
public class IntegraBDOferta {

    private final RepoOferta repoOferta;

    public IntegraBDOferta(RepoOferta repoOferta) {
        this.repoOferta = repoOferta;
    }

    public Optional<Oferta> findById(Long idOferta) {
        return repoOferta.findById(idOferta);
    }

    public Optional<Oferta> findTopBySubastaOrderByValorDescFechaHoraAsc(Subasta subasta) {
        return repoOferta.findTopBySubastaOrderByValorDescFechaHoraAsc(subasta);
    }

    public List<Oferta> findBySubasta(Subasta subasta) {
        return repoOferta.findBySubasta(subasta);
    }

    public List<Oferta> findByClienteCedulaAndEstado(String cedula, EstadoOferta estado) {
        return repoOferta.findByClienteCedulaAndEstado(cedula, estado);
    }

    public Oferta save(Oferta oferta) {
        return repoOferta.save(oferta);
    }
}
