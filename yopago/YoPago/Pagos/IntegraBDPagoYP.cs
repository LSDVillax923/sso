using YoPago.Datos;

namespace YoPago.Pagos;

public class IntegraBDPagoYP
{
    private readonly YoPagoDbContext db;

    public IntegraBDPagoYP(YoPagoDbContext db)
    {
        this.db = db;
    }

    public void Agregar(PagoYP pago)
    {
        db.Pagos.Add(pago);
        db.SaveChanges();
    }

    public PagoYP? BuscarPorNumeroCompra(long numeroCompra) =>
        db.Pagos.Where(p => p.NumeroCompra == numeroCompra).OrderByDescending(p => p.Fecha).FirstOrDefault();

    public List<PagoYP> BuscarNoNotificados() => db.Pagos.Where(p => !p.NotificadoSSO).ToList();

    public void MarcarNotificado(long idPago)
    {
        var pago = db.Pagos.Find(idPago);
        if (pago == null) return;
        pago.NotificadoSSO = true;
        db.SaveChanges();
    }
}
