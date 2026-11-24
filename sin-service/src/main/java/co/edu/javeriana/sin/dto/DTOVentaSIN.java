package co.edu.javeriana.sin.dto;

import java.time.LocalDateTime;

public record DTOVentaSIN(Long numeroCompra, Long idBien, String descripcion, int cantidad, LocalDateTime fechaVenta) {
}
