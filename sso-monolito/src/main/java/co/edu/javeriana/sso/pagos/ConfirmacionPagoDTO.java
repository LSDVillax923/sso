package co.edu.javeriana.sso.pagos;

import java.math.BigDecimal;

/** Lo que YoPago le manda a SSO: cédula, número de compra pagada y valor pagado. */
public record ConfirmacionPagoDTO(String cedula, Long numeroCompra, BigDecimal valorPagado) {
}
