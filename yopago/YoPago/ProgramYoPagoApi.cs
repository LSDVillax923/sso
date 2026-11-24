using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using YoPago.Autenticacion;
using YoPago.Compras;
using YoPago.Datos;
using YoPago.Entrada;
using YoPago.Pagos;

namespace YoPago;

/** Monolito .NET de YoPago (PC1). Su presentación, YoPago.Web, corre en otra máquina (PC2). */
public class ProgramYoPagoApi
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        var cfg = builder.Configuration;

        builder.Services.AddControllers();
        builder.Services.AddDbContext<YoPagoDbContext>(o => o.UseSqlServer(cfg.GetConnectionString("BdYoPago")));
        builder.Services.AddScoped<IntegraBDUsuarioYP>();
        builder.Services.AddScoped<IntegraBDCompraPendiente>();
        builder.Services.AddScoped<IntegraBDPagoYP>();
        builder.Services.AddScoped<SrvAutenticacionYP>();
        builder.Services.AddScoped<SrvComprasPendientes>();
        builder.Services.AddScoped<SrvPagoYP>();
        builder.Services.AddSingleton<ConfigRabbitYP>();
        builder.Services.AddSingleton<PublicadorPagoSSO>();
        builder.Services.AddHostedService<LisenerComprasSSO>();
        builder.Services.AddHostedService<LisenerConsultaPago>();
        builder.Services.AddHostedService<TimerReintentoNotificacion>();

        var clave = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(cfg["Jwt:Clave"]!));
        builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
            .AddJwtBearer(o => o.TokenValidationParameters = new TokenValidationParameters
            {
                ValidateIssuer = false,
                ValidateAudience = false,
                ValidateLifetime = true,
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = clave
            });
        builder.Services.AddAuthorization();

        var app = builder.Build();

        // Crea bd_yopago y los usuarios de prueba la primera vez
        using (var scope = app.Services.CreateScope())
        {
            scope.ServiceProvider.GetRequiredService<YoPagoDbContext>().Database.EnsureCreated();
        }

        app.UseMiddleware<APIRestYP>();
        app.UseAuthentication();
        app.UseAuthorization();
        app.MapControllers();
        app.Run();
    }
}
