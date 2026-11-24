namespace YoPago.Web.Models;

public class ComprobanteVM
{
    public long IdPago { get; set; }
    public long NumeroCompra { get; set; }
    public decimal ValorPagado { get; set; }
    public DateTime Fecha { get; set; }
}
