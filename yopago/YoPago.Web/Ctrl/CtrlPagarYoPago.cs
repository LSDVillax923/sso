using System.Text.Json;
using Microsoft.AspNetCore.Mvc;
using YoPago.Web.Models;
using YoPago.Web.Proxy;

namespace YoPago.Web.Ctrl;

/** Controlador de la pantalla PanPagarYoPago (vistas en Views/PanPagarYoPago). Confirma y registra el pago de una compra SSO. */
public class CtrlPagarYoPago : Controller
{
    private readonly ProxyYoPagoAPI proxy;

    public CtrlPagarYoPago(ProxyYoPagoAPI proxy)
    {
        this.proxy = proxy;
    }

    [HttpGet("/pagar/{numeroCompra:long}")]
    public async Task<IActionResult> Pagar(long numeroCompra)
    {
        var token = HttpContext.Session.GetString("token");
        var cedula = HttpContext.Session.GetString("cedula");
        if (token == null || cedula == null) return Redirect("/");
        var compra = (await proxy.ObtenerPendientesAsync(cedula, token)).FirstOrDefault(c => c.NumeroCompra == numeroCompra);
        if (compra == null) return Redirect("/compras");
        return View("~/Views/PanPagarYoPago/Pagar.cshtml", new PagoVM { NumeroCompra = compra.NumeroCompra, Valor = compra.Valor });
    }

    [HttpPost("/pagar")]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Confirmar(PagoVM vm)
    {
        var token = HttpContext.Session.GetString("token");
        if (token == null) return Redirect("/");
        var comprobante = await proxy.PagarAsync(vm, token);
        if (comprobante == null)
        {
            ViewBag.Error = "No se pudo registrar el pago. Revise que la compra siga pendiente.";
            return View("~/Views/PanPagarYoPago/Pagar.cshtml", vm);
        }
        TempData["comprobante"] = JsonSerializer.Serialize(comprobante);
        return Redirect("/comprobante");
    }

    [HttpGet("/comprobante")]
    public IActionResult Comprobante()
    {
        if (TempData["comprobante"] is not string json) return Redirect("/compras");
        return View("~/Views/PanPagarYoPago/Comprobante.cshtml", JsonSerializer.Deserialize<ComprobanteVM>(json));
    }
}
