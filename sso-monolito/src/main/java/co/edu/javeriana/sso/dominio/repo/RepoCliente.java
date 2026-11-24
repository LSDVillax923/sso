package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoCliente extends JpaRepository<Cliente, String> {

    Optional<Cliente> findByUsuarioAD(String usuarioAD);
}
