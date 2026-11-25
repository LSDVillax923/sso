package co.edu.javeriana.sso.distribucion;

import co.edu.javeriana.sso.distribucion.IntegraSEN;
import co.edu.javeriana.sso.distribucion.IntegraSIN;
import co.edu.javeriana.sso.dominio.entidad.Bien;
import co.edu.javeriana.sso.dominio.entidad.Cliente;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.repo.RepoCompra;
import co.edu.javeriana.sso.dominio.mensaje.MsgEnvioSEN;
import co.edu.javeriana.sso.dominio.mensaje.MsgVentaSIN;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Envía la compra pagada a Inventarios (SIN) y a Envíos (SEN). */
@Service
public class SrvDistribuirCompra {

    private final IntegraSIN integraSIN;
    private final IntegraSEN integraSEN;
    private final RepoCompra repoCompra;

    public SrvDistribuirCompra(IntegraSIN integraSIN, IntegraSEN integraSEN, RepoCompra repoCompra) {
        this.integraSIN = integraSIN;
        this.integraSEN = integraSEN;
        this.repoCompra = repoCompra;
    }

    @Transactional
    public void distribuir(Compra compra) {
        integraSIN.enviarVenta(ventaDe(compra));
        integraSEN.enviarEnvio(envioDe(compra));
        compra.setEstado(EstadoCompra.DISTRIBUIDA);
        repoCompra.save(compra);
    }

    private MsgVentaSIN ventaDe(Compra compra) {
        Bien bien = compra.getOferta().getSubasta().getBien();
        return new MsgVentaSIN(compra.getNumeroCompra(), bien.getIdBien(), bien.getNombre(), 1, LocalDateTime.now());
    }

    private MsgEnvioSEN envioDe(Compra compra) {
        Bien bien = compra.getOferta().getSubasta().getBien();
        Cliente cliente = compra.getCliente();
        return new MsgEnvioSEN(compra.getNumeroCompra(), bien.getIdBien(), bien.getNombre(), cliente.getCedula(),
                cliente.getNombre(), cliente.getDireccion(), cliente.getCelular());
    }
}
