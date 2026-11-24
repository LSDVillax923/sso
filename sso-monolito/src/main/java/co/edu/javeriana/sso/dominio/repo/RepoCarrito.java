package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.CarritoSubasta;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoCarrito extends JpaRepository<CarritoSubasta, Long> {

    Optional<CarritoSubasta> findByClienteCedula(String cedula);
}
