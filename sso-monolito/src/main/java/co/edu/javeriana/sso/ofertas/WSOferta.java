package co.edu.javeriana.sso.ofertas;

import co.edu.javeriana.sso.ofertas.OfertaDTO;
import co.edu.javeriana.sso.ofertas.PeticionOfertaDTO;
import co.edu.javeriana.sso.ofertas.RespuestaOfertaDTO;
import co.edu.javeriana.sso.ofertas.SrvOferta;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ofertas")
public class WSOferta {

    private final SrvOferta srvOferta;

    public WSOferta(SrvOferta srvOferta) {
        this.srvOferta = srvOferta;
    }

    @PostMapping
    public ResponseEntity<RespuestaOfertaDTO> registrar(@AuthenticationPrincipal String cedula,
                                                        @RequestBody PeticionOfertaDTO peticion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(srvOferta.registrarOfertas(cedula, peticion));
    }

    @GetMapping("/ganadoras")
    public List<OfertaDTO> ganadoras(@AuthenticationPrincipal String cedula) {
        return srvOferta.consultarGanadoras(cedula);
    }
}
