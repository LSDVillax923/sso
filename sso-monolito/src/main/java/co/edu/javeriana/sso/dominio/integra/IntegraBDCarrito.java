package co.edu.javeriana.sso.dominio.integra;

import co.edu.javeriana.sso.dominio.entidad.CarritoSubasta;
import co.edu.javeriana.sso.dominio.repo.RepoCarrito;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Salida del monolito hacia bd_sso (JDBC 5432) para el carrito de subastas del cliente. Los Srv no tocan el repositorio. */
@Component
public class IntegraBDCarrito {

    private final RepoCarrito repoCarrito;

    public IntegraBDCarrito(RepoCarrito repoCarrito) {
        this.repoCarrito = repoCarrito;
    }

    public Optional<CarritoSubasta> findByClienteCedula(String cedula) {
        return repoCarrito.findByClienteCedula(cedula);
    }

    public CarritoSubasta save(CarritoSubasta carrito) {
        return repoCarrito.save(carrito);
    }
}
