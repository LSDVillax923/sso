namespace YoPago.Pagos;

/** Lo que YoPago publica en pago.confirmado: cédula, número de la compra pagada y valor pagado. */
public record ConfirmacionPagoYPDTO(string Cedula, long NumeroCompra, decimal ValorPagado);
