package co.edu.javeriana.sso.dominio.integra;

import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.entidad.Oferta;
import co.edu.javeriana.sso.dominio.repo.RepoCompra;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Salida del monolito hacia bd_sso (JDBC 5432) para las compras; bloquear() toma la fila con lock para los dos ack. Los Srv no tocan el repositorio. */
@Component
public class IntegraBDCompra {

    private final RepoCompra repoCompra;

    public IntegraBDCompra(RepoCompra repoCompra) {
        this.repoCompra = repoCompra;
    }

    public Optional<Compra> findByNumeroCompra(Long numeroCompra) {
        return repoCompra.findByNumeroCompra(numeroCompra);
    }

    public Optional<Compra> bloquear(Long numeroCompra) {
        return repoCompra.bloquear(numeroCompra);
    }

    public boolean existsByOferta(Oferta oferta) {
        return repoCompra.existsByOferta(oferta);
    }

    public List<Compra> findByEstadoAndProximoIntentoBefore(EstadoCompra estado, LocalDateTime fecha) {
        return repoCompra.findByEstadoAndProximoIntentoBefore(estado, fecha);
    }

    public List<Compra> findByEstadoAndFechaActualizacionBefore(EstadoCompra estado, LocalDateTime fecha) {
        return repoCompra.findByEstadoAndFechaActualizacionBefore(estado, fecha);
    }

    public Compra save(Compra compra) {
        return repoCompra.save(compra);
    }
}
