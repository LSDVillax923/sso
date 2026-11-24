package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import java.math.BigDecimal;

public record CompraDTO(Long numeroCompra, String nombreBien, BigDecimal valorOferta, BigDecimal impuestos,
                        BigDecimal valorTotal, EstadoCompra estado) {
}
