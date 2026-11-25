using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace YoPago.Compras;

/** YoPago.Web (PC2) lista aquí las compras SSO pendientes del cliente autenticado. */
[ApiController]
[Route("api/compras-pendientes")]
[Authorize]
public class WSComprasPendientes : ControllerBase
{
    private readonly SrvComprasPendientes srv;

    public WSComprasPendientes(SrvComprasPendientes srv)
    {
        this.srv = srv;
    }

    [HttpGet]
    public ActionResult<List<CompraPendienteYPDTO>> Listar()
    {
        var cedulaToken = User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? User.FindFirst("sub")?.Value;
        if (cedulaToken == null) return Forbid();
        return srv.Listar(cedulaToken);
    }
}
