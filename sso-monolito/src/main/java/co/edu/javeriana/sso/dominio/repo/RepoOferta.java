package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.EstadoOferta;
import co.edu.javeriana.sso.dominio.entidad.Oferta;
import co.edu.javeriana.sso.dominio.entidad.Subasta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoOferta extends JpaRepository<Oferta, Long> {

    /** Gana la oferta más alta; si hay empate, la que llegó primero. */
    Optional<Oferta> findTopBySubastaOrderByValorDescFechaHoraAsc(Subasta subasta);

    List<Oferta> findBySubasta(Subasta subasta);

    List<Oferta> findByClienteCedulaAndEstado(String cedula, EstadoOferta estado);
}
