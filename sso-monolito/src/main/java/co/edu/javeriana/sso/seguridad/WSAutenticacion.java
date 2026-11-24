package co.edu.javeriana.sso.seguridad;

import co.edu.javeriana.sso.seguridad.LoginDTO;
import co.edu.javeriana.sso.seguridad.TokenDTO;
import co.edu.javeriana.sso.seguridad.SrvAutenticacion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class WSAutenticacion {

    private final SrvAutenticacion srvAutenticacion;

    public WSAutenticacion(SrvAutenticacion srvAutenticacion) {
        this.srvAutenticacion = srvAutenticacion;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(srvAutenticacion.autenticar(dto));
    }
}
