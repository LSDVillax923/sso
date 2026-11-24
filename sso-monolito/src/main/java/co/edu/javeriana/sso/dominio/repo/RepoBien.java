package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.Bien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoBien extends JpaRepository<Bien, Long> {
}
