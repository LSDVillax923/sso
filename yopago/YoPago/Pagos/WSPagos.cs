using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace YoPago.Pagos;

/** El cliente paga desde YoPago.Web (PC2) con su token. */
[ApiController]
[Route("api/pagos")]
[Authorize]
public class WSPagos : ControllerBase
{
    private readonly SrvPagoYP srv;

    public WSPagos(SrvPagoYP srv)
    {
        this.srv = srv;
    }

    [HttpPost]
    public ActionResult<ComprobanteYPDTO> Pagar(PagoYPDTO dto)
    {
        var cedula = User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? User.FindFirst("sub")?.Value;
        if (cedula == null) return Unauthorized();
        var comprobante = srv.Pagar(dto, cedula);
        if (comprobante == null) return Conflict("La compra no está pendiente o no es suya");
        return comprobante;
    }
}
