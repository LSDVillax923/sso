using Microsoft.AspNetCore.Mvc;
using YoPago.Web.Proxy;

namespace YoPago.Web.Ctrl;

/** Controlador de la pantalla PanComprasPendientes (vistas en Views/PanComprasPendientes). Muestra las compras SSO pendientes por pago del cliente. */
public class CtrlComprasPendientes : Controller
{
    private readonly ProxyYoPagoAPI proxy;

    public CtrlComprasPendientes(ProxyYoPagoAPI proxy)
    {
        this.proxy = proxy;
    }

    [HttpGet("/compras")]
    public async Task<IActionResult> Index()
    {
        var token = HttpContext.Session.GetString("token");
        var cedula = HttpContext.Session.GetString("cedula");
        if (token == null || cedula == null) return Redirect("/");
        try
        {
            return View("~/Views/PanComprasPendientes/Index.cshtml", await proxy.ObtenerPendientesAsync(cedula, token));
        }
        catch (HttpRequestException)
        {
            HttpContext.Session.Clear();
            return Redirect("/");
        }
    }
}
