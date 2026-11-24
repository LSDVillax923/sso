package co.edu.javeriana.sso.catalogo;

import co.edu.javeriana.sso.catalogo.BienDTO;
import co.edu.javeriana.sso.catalogo.SrvCatalogo;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogo")
public class WSCatalogo {

    private final SrvCatalogo srvCatalogo;

    public WSCatalogo(SrvCatalogo srvCatalogo) {
        this.srvCatalogo = srvCatalogo;
    }

    @GetMapping
    public List<BienDTO> buscar(@RequestParam(defaultValue = "") String filtro) {
        return srvCatalogo.buscarBienesEnSubasta(filtro);
    }
}
