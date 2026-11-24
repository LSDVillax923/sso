package co.edu.javeriana.sso.dominio.integra;

import co.edu.javeriana.sso.dominio.entidad.AckCompra;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.SistemaDestino;
import co.edu.javeriana.sso.dominio.repo.RepoAckCompra;
import org.springframework.stereotype.Component;

/** Salida del monolito hacia bd_sso (JDBC 5432) para los ack de SIN y SEN. Los Srv no tocan el repositorio. */
@Component
public class IntegraBDAckCompra {

    private final RepoAckCompra repoAckCompra;

    public IntegraBDAckCompra(RepoAckCompra repoAckCompra) {
        this.repoAckCompra = repoAckCompra;
    }

    public boolean existsByCompraAndSistema(Compra compra, SistemaDestino sistema) {
        return repoAckCompra.existsByCompraAndSistema(compra, sistema);
    }

    public long countByCompra(Compra compra) {
        return repoAckCompra.countByCompra(compra);
    }

    public AckCompra save(AckCompra ack) {
        return repoAckCompra.save(ack);
    }
}
