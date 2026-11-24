package co.edu.javeriana.sin.srb;

import co.edu.javeriana.sin.dto.DTOVentaSIN;
import co.edu.javeriana.sin.integra.IntegraAckSIN;
import co.edu.javeriana.sin.integra.IntegraBDInventario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regla de negocio de SIN: guarda la venta y descuenta el inventario una sola vez, y siempre confirma a SSO. */
@Service
public class SRBAlmacenarVenta {

    private final IntegraBDInventario integraBDInventario;
    private final IntegraAckSIN integraAckSIN;

    public SRBAlmacenarVenta(IntegraBDInventario integraBDInventario, IntegraAckSIN integraAckSIN) {
        this.integraBDInventario = integraBDInventario;
        this.integraAckSIN = integraAckSIN;
    }

    /** Almacena la información que llega. Si SSO la reenvía, no se descuenta dos veces pero se vuelve a confirmar. */
    @Transactional
    public void almacenarVenta(DTOVentaSIN dto) {
        if (!integraBDInventario.ventaRegistrada(dto.numeroCompra())) {
            integraBDInventario.registrarVenta(dto);
            integraBDInventario.descontar(dto.idBien(), dto.descripcion(), dto.cantidad());
        }
        integraAckSIN.enviarAck(dto.numeroCompra());
    }
}
