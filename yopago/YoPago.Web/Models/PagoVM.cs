namespace YoPago.Web.Models;

public class PagoVM
{
    public long NumeroCompra { get; set; }
    public decimal Valor { get; set; }
    public string MedioPago { get; set; } = "PSE";
}
