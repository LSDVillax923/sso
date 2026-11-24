using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace YoPago.Datos;

/** Compra que SSO publicó en el tópico y que el cliente todavía no paga. El número lo pone SSO. */
[Table("compra_pendiente")]
public class CompraPendiente
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    public long NumeroCompra { get; set; }
    [MaxLength(20)]
    public string Cedula { get; set; } = "";
    public decimal Valor { get; set; }
    [MaxLength(20)]
    public string Estado { get; set; } = "PENDIENTE";
    public DateTime FechaRegistro { get; set; }
}
