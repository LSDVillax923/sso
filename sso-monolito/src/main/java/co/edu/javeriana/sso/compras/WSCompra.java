package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.compras.CompraDTO;
import co.edu.javeriana.sso.compras.PeticionCompraDTO;
import co.edu.javeriana.sso.compras.RespuestaCompraDTO;
import co.edu.javeriana.sso.compras.SrvCompra;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compras")
public class WSCompra {

    private final SrvCompra srvCompra;

    public WSCompra(SrvCompra srvCompra) {
        this.srvCompra = srvCompra;
    }

    /** 202: la verificación OFAC sigue por cola; el cliente recibe un SMS cuando pueda pagar. */
    @PostMapping
    public ResponseEntity<RespuestaCompraDTO> iniciar(@AuthenticationPrincipal String cedula,
                                                      @RequestBody PeticionCompraDTO peticion) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(srvCompra.iniciarCompra(cedula, peticion.idOferta()));
    }

    @GetMapping("/{numeroCompra}")
    public CompraDTO consultar(@AuthenticationPrincipal String cedula, @PathVariable Long numeroCompra) {
        return srvCompra.consultar(cedula, numeroCompra);
    }
}
