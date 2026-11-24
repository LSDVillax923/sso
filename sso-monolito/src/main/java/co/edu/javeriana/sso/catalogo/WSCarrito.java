package co.edu.javeriana.sso.catalogo;

import co.edu.javeriana.sso.catalogo.ItemCarritoDTO;
import co.edu.javeriana.sso.catalogo.SrvCarrito;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carrito")
public class WSCarrito {

    private final SrvCarrito srvCarrito;

    public WSCarrito(SrvCarrito srvCarrito) {
        this.srvCarrito = srvCarrito;
    }

    @PostMapping("/{idSubasta}")
    public ResponseEntity<Void> agregar(@AuthenticationPrincipal String cedula, @PathVariable Long idSubasta) {
        srvCarrito.agregarItem(cedula, idSubasta);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ItemCarritoDTO> listar(@AuthenticationPrincipal String cedula) {
        return srvCarrito.listar(cedula);
    }

    @DeleteMapping("/{idItem}")
    public ResponseEntity<Void> quitar(@AuthenticationPrincipal String cedula, @PathVariable Long idItem) {
        srvCarrito.quitar(cedula, idItem);
        return ResponseEntity.noContent().build();
    }
}
