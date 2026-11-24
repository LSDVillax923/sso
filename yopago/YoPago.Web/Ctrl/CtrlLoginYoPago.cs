using Microsoft.AspNetCore.Mvc;
using YoPago.Web.Models;
using YoPago.Web.Proxy;

namespace YoPago.Web.Ctrl;

/** Controlador de la pantalla PanLoginYoPago (vistas en Views/PanLoginYoPago). El cliente se identifica con cédula y contraseña. */
public class CtrlLoginYoPago : Controller
{
    private readonly ProxyYoPagoAPI proxy;

    public CtrlLoginYoPago(ProxyYoPagoAPI proxy)
    {
        this.proxy = proxy;
    }

    [HttpGet("/")]
    public IActionResult Index() => View("~/Views/PanLoginYoPago/Index.cshtml", new LoginVM());

    [HttpPost("/ingresar")]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Ingresar(LoginVM vm)
    {
        var token = await proxy.LoginAsync(vm);
        if (token == null)
        {
            ViewBag.Error = "Cédula o contraseña incorrecta";
            return View("~/Views/PanLoginYoPago/Index.cshtml", vm);
        }
        HttpContext.Session.SetString("token", token);
        HttpContext.Session.SetString("cedula", vm.Cedula);
        return Redirect("/compras");
    }
}
