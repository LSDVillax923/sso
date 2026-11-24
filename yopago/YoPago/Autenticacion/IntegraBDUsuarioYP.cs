using YoPago.Datos;

namespace YoPago.Autenticacion;

public class IntegraBDUsuarioYP
{
    private readonly YoPagoDbContext db;

    public IntegraBDUsuarioYP(YoPagoDbContext db)
    {
        this.db = db;
    }

    public UsuarioYP? BuscarPorCedula(string cedula) => db.Usuarios.Find(cedula);
}
