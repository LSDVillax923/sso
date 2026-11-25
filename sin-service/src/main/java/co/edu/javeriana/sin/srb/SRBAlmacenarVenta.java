package co.edu.javeriana.sin.srb;

import co.edu.javeriana.sin.dto.DTOVentaSIN;
import co.edu.javeriana.sin.entidad.ItemInventario;
import co.edu.javeriana.sin.entidad.VentaRecibida;
import co.edu.javeriana.sin.integra.IntegraAckSIN;
import co.edu.javeriana.sin.repo.RepoInventario;
import co.edu.javeriana.sin.repo.RepoVentaRecibida;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regla de negocio de SIN: guarda la venta y descuenta el inventario una sola vez, y siempre confirma a SSO. */
@Service
public class SRBAlmacenarVenta {

    private final RepoVentaRecibida repoVentaRecibida;
    private final RepoInventario repoInventario;
    private final IntegraAckSIN integraAckSIN;

    public SRBAlmacenarVenta(RepoVentaRecibida repoVentaRecibida, RepoInventario repoInventario, IntegraAckSIN integraAckSIN) {
        this.repoVentaRecibida = repoVentaRecibida;
        this.repoInventario = repoInventario;
        this.integraAckSIN = integraAckSIN;
    }

    /** Almacena la información que llega. Si SSO la reenvía, no se descuenta dos veces pero se vuelve a confirmar. */
    @Transactional
    public void almacenarVenta(DTOVentaSIN dto) {
        if (!repoVentaRecibida.existsByNumeroCompra(dto.numeroCompra())) {
            registrarVenta(dto);
            descontar(dto.idBien(), dto.descripcion(), dto.cantidad());
        }
        integraAckSIN.enviarAck(dto.numeroCompra());
    }

    private void registrarVenta(DTOVentaSIN dto) {
        VentaRecibida venta = new VentaRecibida();
        venta.setNumeroCompra(dto.numeroCompra());
        venta.setIdBien(dto.idBien());
        venta.setDescripcion(dto.descripcion());
        venta.setCantidad(dto.cantidad());
        venta.setFechaRecepcion(LocalDateTime.now());
        repoVentaRecibida.save(venta);
    }

    private void descontar(Long idBien, String descripcion, int cantidad) {
        ItemInventario item = repoInventario.findById(idBien).orElseGet(() -> {
            ItemInventario nuevo = new ItemInventario();
            nuevo.setIdBien(idBien);
            nuevo.setDescripcion(descripcion);
            nuevo.setCantidadDisponible(cantidad);
            return nuevo;
        });
        item.setCantidadDisponible(Math.max(0, item.getCantidadDisponible() - cantidad));
        item.setEstado(item.getCantidadDisponible() == 0 ? "VENDIDO" : "DISPONIBLE");
        repoInventario.save(item);
    }
}
