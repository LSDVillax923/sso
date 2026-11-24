package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;

public record RespuestaCompraDTO(Long numeroCompra, EstadoCompra estado, String mensaje) {
}
