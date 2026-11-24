package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.Pago;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoPago extends JpaRepository<Pago, Long> {

    Optional<Pago> findByCompra(Compra compra);
}
