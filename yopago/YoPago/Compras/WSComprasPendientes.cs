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

    [HttpGet("{cedula}")]
    public ActionResult<List<CompraPendienteYPDTO>> Listar(string cedula)
    {
        var cedulaToken = User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? User.FindFirst("sub")?.Value;
        if (cedulaToken != cedula) return Forbid();
        return srv.Listar(cedula);
    }
}
