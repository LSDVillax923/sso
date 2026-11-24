using Microsoft.EntityFrameworkCore;

namespace YoPago.Datos;

public class YoPagoDbContext : DbContext
{
    public YoPagoDbContext(DbContextOptions<YoPagoDbContext> options) : base(options)
    {
    }

    public DbSet<UsuarioYP> Usuarios => Set<UsuarioYP>();
    public DbSet<CompraPendiente> ComprasPendientes => Set<CompraPendiente>();
    public DbSet<PagoYP> Pagos => Set<PagoYP>();

    protected override void OnModelCreating(ModelBuilder mb)
    {
        mb.Entity<CompraPendiente>().Property(c => c.Valor).HasPrecision(18, 2);
        mb.Entity<PagoYP>().Property(p => p.ValorPagado).HasPrecision(18, 2);
        mb.Entity<PagoYP>().HasIndex(p => p.NumeroCompra);

        // Usuarios de prueba, contraseña 1234 (SHA-256). Las cédulas coinciden con los clientes de bd_sso.
        const string hash1234 = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        mb.Entity<UsuarioYP>().HasData(
            new UsuarioYP { Cedula = "1010", Nombre = "Ana Gómez", Email = "ana@correo.com", ContrasenaHash = hash1234 },
            new UsuarioYP { Cedula = "2020", Nombre = "Luis Pérez", Email = "luis@correo.com", ContrasenaHash = hash1234 },
            new UsuarioYP { Cedula = "8888", Nombre = "Cliente en revisión OFAC", Email = "revision@correo.com", ContrasenaHash = hash1234 });
    }
}
