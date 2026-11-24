package co.edu.javeriana.sso.dominio.mensaje;

import java.time.LocalDateTime;

public record MsgVentaSIN(Long numeroCompra, Long idBien, String descripcion, int cantidad, LocalDateTime fechaVenta) {
}
