using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace YoPago.Datos;

[Table("pago_yp")]
public class PagoYP
{
    [Key]
    public long IdPago { get; set; }
    public long NumeroCompra { get; set; }
    [MaxLength(20)]
    public string Cedula { get; set; } = "";
    public decimal ValorPagado { get; set; }
    public DateTime Fecha { get; set; }
    /** false mientras la confirmación no se haya publicado en el tópico; TimerReintentoNotificacion la reintenta. */
    public bool NotificadoSSO { get; set; }
}
