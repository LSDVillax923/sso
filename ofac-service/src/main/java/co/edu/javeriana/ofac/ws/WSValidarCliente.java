package co.edu.javeriana.ofac.ws;

import co.edu.javeriana.ofac.dto.RespuestaOFAC;
import co.edu.javeriana.ofac.srv.SrvOFAC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ofac")
public class WSValidarCliente {

    private final SrvOFAC srvOFAC;

    public WSValidarCliente(SrvOFAC srvOFAC) {
        this.srvOFAC = srvOFAC;
    }

    @GetMapping("/clientes/{cedula}")
    public ResponseEntity<RespuestaOFAC> validar(@PathVariable String cedula) {
        return ResponseEntity.ok(srvOFAC.validar(cedula));
    }
}
