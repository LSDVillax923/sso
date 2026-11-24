package co.edu.javeriana.sin.integra;

import co.edu.javeriana.sin.dto.DTOVentaSIN;
import co.edu.javeriana.sin.entidad.ItemInventario;
import co.edu.javeriana.sin.entidad.VentaRecibida;
import co.edu.javeriana.sin.repo.RepoInventario;
import co.edu.javeriana.sin.repo.RepoVentaRecibida;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/** Única salida de SIN hacia bd_sin (JDBC 5432): guarda la venta recibida y descuenta el inventario. */
@Component
public class IntegraBDInventario {

    private final RepoVentaRecibida repoVentaRecibida;
    private final RepoInventario repoInventario;

    public IntegraBDInventario(RepoVentaRecibida repoVentaRecibida, RepoInventario repoInventario) {
        this.repoVentaRecibida = repoVentaRecibida;
        this.repoInventario = repoInventario;
    }

    public boolean ventaRegistrada(Long numeroCompra) {
        return repoVentaRecibida.existsByNumeroCompra(numeroCompra);
    }

    public void registrarVenta(DTOVentaSIN dto) {
        VentaRecibida venta = new VentaRecibida();
        venta.setNumeroCompra(dto.numeroCompra());
        venta.setIdBien(dto.idBien());
        venta.setDescripcion(dto.descripcion());
        venta.setCantidad(dto.cantidad());
        venta.setFechaRecepcion(LocalDateTime.now());
        repoVentaRecibida.save(venta);
    }

    public void descontar(Long idBien, String descripcion, int cantidad) {
        ItemInventario item = repoInventario.findByIdBien(idBien).orElseGet(() -> {
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
