using Microsoft.AspNetCore.Mvc;

namespace YoPago.Autenticacion;

[ApiController]
[Route("api/auth")]
public class WSAutenticacionYP : ControllerBase
{
    private readonly SrvAutenticacionYP srv;

    public WSAutenticacionYP(SrvAutenticacionYP srv)
    {
        this.srv = srv;
    }

    [HttpPost]
    public ActionResult<string> Login(LoginYPDTO dto)
    {
        var token = srv.Autenticar(dto.Cedula, dto.Contrasena);
        if (token == null) return Unauthorized();
        return Ok(token);
    }
}
