package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.AckCompra;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.SistemaDestino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoAckCompra extends JpaRepository<AckCompra, Long> {

    boolean existsByCompraAndSistema(Compra compra, SistemaDestino sistema);

    long countByCompra(Compra compra);
}
