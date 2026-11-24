using YoPago.Datos;

namespace YoPago.Compras;

public class SrvComprasPendientes
{
    private readonly IntegraBDCompraPendiente integraBDCompra;

    public SrvComprasPendientes(IntegraBDCompraPendiente integraBDCompra)
    {
        this.integraBDCompra = integraBDCompra;
    }

    /** Idempotente: si el mensaje llega dos veces la compra no se duplica. */
    public void Registrar(CompraPendienteYPDTO dto)
    {
        if (integraBDCompra.BuscarPorNumero(dto.NumeroCompra) != null) return;
        integraBDCompra.Agregar(new CompraPendiente
        {
            NumeroCompra = dto.NumeroCompra,
            Cedula = dto.Cedula,
            Valor = dto.Valor,
            Estado = "PENDIENTE",
            FechaRegistro = DateTime.Now
        });
    }

    public List<CompraPendienteYPDTO> Listar(string cedula) =>
        integraBDCompra.BuscarPendientes(cedula)
            .Select(c => new CompraPendienteYPDTO(c.Cedula, c.NumeroCompra, c.Valor))
            .ToList();
}
