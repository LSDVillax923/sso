package co.edu.javeriana.sso.seguridad;

import co.edu.javeriana.sso.dominio.entidad.Cliente;
import co.edu.javeriana.sso.dominio.error.ErrorNegocio;
import co.edu.javeriana.sso.dominio.repo.RepoCliente;
import co.edu.javeriana.sso.seguridad.IntegraAD;
import co.edu.javeriana.sso.seguridad.LoginDTO;
import co.edu.javeriana.sso.seguridad.TokenDTO;
import co.edu.javeriana.sso.seguridad.UtilJWT;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SrvAutenticacion {

    private final IntegraAD integraAD;
    private final RepoCliente repoCliente;
    private final UtilJWT utilJWT;

    public SrvAutenticacion(IntegraAD integraAD, RepoCliente repoCliente, UtilJWT utilJWT) {
        this.integraAD = integraAD;
        this.repoCliente = repoCliente;
        this.utilJWT = utilJWT;
    }

    public TokenDTO autenticar(LoginDTO dto) {
        if (dto == null || dto.usuario() == null || dto.clave() == null || dto.clave().isBlank()
                || !integraAD.validarCredenciales(dto.usuario(), dto.clave())) {
            throw new ErrorNegocio(HttpStatus.UNAUTHORIZED, "Usuario o clave incorrectos");
        }
        Cliente cliente = repoCliente.findByUsuarioAD(dto.usuario())
                .orElseThrow(() -> new ErrorNegocio(HttpStatus.FORBIDDEN, "El usuario no está registrado como cliente de SSO"));
        return new TokenDTO(utilJWT.generarToken(cliente), cliente.getCedula(), cliente.getNombre());
    }
}
