package co.edu.javeriana.sso.dominio.integra;

import co.edu.javeriana.sso.dominio.entidad.Cliente;
import co.edu.javeriana.sso.dominio.repo.RepoCliente;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Salida del monolito hacia bd_sso (JDBC 5432) para los clientes (usuario de AD y cédula). Los Srv no tocan el repositorio. */
@Component
public class IntegraBDCliente {

    private final RepoCliente repoCliente;

    public IntegraBDCliente(RepoCliente repoCliente) {
        this.repoCliente = repoCliente;
    }

    public Optional<Cliente> findById(String cedula) {
        return repoCliente.findById(cedula);
    }

    public Optional<Cliente> findByUsuarioAD(String usuarioAD) {
        return repoCliente.findByUsuarioAD(usuarioAD);
    }
}
