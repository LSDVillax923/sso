using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using Microsoft.IdentityModel.Tokens;

namespace YoPago.Autenticacion;

public class SrvAutenticacionYP
{
    private readonly IntegraBDUsuarioYP integraBDUsuario;
    private readonly IConfiguration cfg;

    public SrvAutenticacionYP(IntegraBDUsuarioYP integraBDUsuario, IConfiguration cfg)
    {
        this.integraBDUsuario = integraBDUsuario;
        this.cfg = cfg;
    }

    /** Devuelve el token JWT o null si la cédula o la contraseña no coinciden. */
    public string? Autenticar(string cedula, string contrasena)
    {
        var usuario = integraBDUsuario.BuscarPorCedula(cedula);
        if (usuario == null || usuario.ContrasenaHash != Hash(contrasena)) return null;

        var clave = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(cfg["Jwt:Clave"]!));
        var token = new JwtSecurityToken(
            claims: new[] { new Claim(JwtRegisteredClaimNames.Sub, usuario.Cedula), new Claim("nombre", usuario.Nombre) },
            expires: DateTime.UtcNow.AddHours(1),
            signingCredentials: new SigningCredentials(clave, SecurityAlgorithms.HmacSha256));
        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    public static string Hash(string texto) =>
        Convert.ToHexString(SHA256.HashData(Encoding.UTF8.GetBytes(texto))).ToLowerInvariant();
}
