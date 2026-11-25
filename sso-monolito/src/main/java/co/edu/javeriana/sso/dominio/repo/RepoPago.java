package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoPago extends JpaRepository<Pago, Long> {
}
