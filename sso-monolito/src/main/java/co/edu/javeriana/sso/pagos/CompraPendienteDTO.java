package co.edu.javeriana.sso.pagos;

import java.math.BigDecimal;

/** Lo que SSO le manda a YoPago: cédula del cliente, número de compra y valor. */
public record CompraPendienteDTO(String cedula, Long numeroCompra, BigDecimal valor) {
}
