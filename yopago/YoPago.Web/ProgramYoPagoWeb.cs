using YoPago.Web.Proxy;

namespace YoPago.Web;

/** Presentación .NET (PC2): solo pantallas y llamadas al monolito YoPago.dll, que corre en PC1. */
public class ProgramYoPagoWeb
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);

        builder.Services.AddControllersWithViews();
        builder.Services.AddDistributedMemoryCache();
        builder.Services.AddSession(o =>
        {
            o.IdleTimeout = TimeSpan.FromMinutes(20);
            o.Cookie.HttpOnly = true;
            o.Cookie.IsEssential = true;
        });
        builder.Services.AddHttpClient<ProxyYoPagoAPI>(c => c.BaseAddress = new Uri(builder.Configuration["YoPagoApi:Url"]!));

        var app = builder.Build();
        app.UseStaticFiles();
        app.UseRouting();
        app.UseSession();
        app.MapControllers();
        app.Run();
    }
}
