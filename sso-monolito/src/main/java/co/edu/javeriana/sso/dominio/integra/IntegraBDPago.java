package co.edu.javeriana.sso.dominio.integra;

import co.edu.javeriana.sso.dominio.entidad.Pago;
import co.edu.javeriana.sso.dominio.repo.RepoPago;
import org.springframework.stereotype.Component;

/** Salida del monolito hacia bd_sso (JDBC 5432) para los pagos confirmados por YoPago. Los Srv no tocan el repositorio. */
@Component
public class IntegraBDPago {

    private final RepoPago repoPago;

    public IntegraBDPago(RepoPago repoPago) {
        this.repoPago = repoPago;
    }

    public Pago save(Pago pago) {
        return repoPago.save(pago);
    }
}
