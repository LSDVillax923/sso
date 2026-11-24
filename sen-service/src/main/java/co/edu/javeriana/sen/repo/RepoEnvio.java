package co.edu.javeriana.sen.repo;

import co.edu.javeriana.sen.entidad.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoEnvio extends JpaRepository<Envio, Long> {

    boolean existsByNumeroCompra(Long numeroCompra);
}
