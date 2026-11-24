namespace YoPago.Compras;

/** Lo que SSO publica en pago.pendiente: cédula del cliente, número de compra y valor. */
public record CompraPendienteYPDTO(string Cedula, long NumeroCompra, decimal Valor);
