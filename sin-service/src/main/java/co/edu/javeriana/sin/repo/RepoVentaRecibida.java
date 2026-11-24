package co.edu.javeriana.sin.repo;

import co.edu.javeriana.sin.entidad.VentaRecibida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoVentaRecibida extends JpaRepository<VentaRecibida, Long> {

    boolean existsByNumeroCompra(Long numeroCompra);
}
