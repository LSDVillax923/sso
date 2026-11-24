using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace YoPago.Datos;

[Table("usuario_yp")]
public class UsuarioYP
{
    [Key]
    [MaxLength(20)]
    public string Cedula { get; set; } = "";
    public string Nombre { get; set; } = "";
    public string Email { get; set; } = "";
    public string ContrasenaHash { get; set; } = "";
}
