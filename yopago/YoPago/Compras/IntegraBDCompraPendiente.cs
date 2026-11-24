using YoPago.Datos;

namespace YoPago.Compras;

public class IntegraBDCompraPendiente
{
    private readonly YoPagoDbContext db;

    public IntegraBDCompraPendiente(YoPagoDbContext db)
    {
        this.db = db;
    }

    public void Agregar(CompraPendiente compra)
    {
        db.ComprasPendientes.Add(compra);
        db.SaveChanges();
    }

    public List<CompraPendiente> BuscarPendientes(string cedula) =>
        db.ComprasPendientes.Where(c => c.Cedula == cedula && c.Estado == "PENDIENTE")
            .OrderBy(c => c.FechaRegistro)
            .ToList();

    public CompraPendiente? BuscarPorNumero(long numeroCompra) => db.ComprasPendientes.Find(numeroCompra);

    public void MarcarPagada(long numeroCompra)
    {
        var compra = db.ComprasPendientes.Find(numeroCompra);
        if (compra == null) return;
        compra.Estado = "PAGADA";
        db.SaveChanges();
    }
}
