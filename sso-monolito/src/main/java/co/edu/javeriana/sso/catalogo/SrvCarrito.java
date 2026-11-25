package co.edu.javeriana.sso.catalogo;

import co.edu.javeriana.sso.catalogo.ItemCarritoDTO;
import co.edu.javeriana.sso.dominio.entidad.CarritoSubasta;
import co.edu.javeriana.sso.dominio.entidad.Cliente;
import co.edu.javeriana.sso.dominio.entidad.ItemCarrito;
import co.edu.javeriana.sso.dominio.entidad.Subasta;
import co.edu.javeriana.sso.dominio.error.ErrorNegocio;
import co.edu.javeriana.sso.dominio.repo.RepoCarrito;
import co.edu.javeriana.sso.dominio.repo.RepoCliente;
import co.edu.javeriana.sso.dominio.repo.RepoSubasta;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvCarrito {

    private final RepoCarrito repoCarrito;
    private final RepoSubasta repoSubasta;
    private final RepoCliente repoCliente;

    public SrvCarrito(RepoCarrito repoCarrito, RepoSubasta repoSubasta, RepoCliente repoCliente) {
        this.repoCarrito = repoCarrito;
        this.repoSubasta = repoSubasta;
        this.repoCliente = repoCliente;
    }

    @Transactional
    public void agregarItem(String cedula, Long idSubasta) {
        Subasta subasta = repoSubasta.findById(idSubasta)
                .orElseThrow(() -> new ErrorNegocio("La subasta no existe"));
        if (!subasta.estaAbierta(LocalDateTime.now())) {
            throw new ErrorNegocio("La subasta no está abierta");
        }
        CarritoSubasta carrito = repoCarrito.findByClienteCedula(cedula).orElseGet(() -> nuevoCarrito(cedula));
        boolean repetido = carrito.getItems().stream().anyMatch(i -> i.getSubasta().getIdSubasta().equals(idSubasta));
        if (repetido) {
            throw new ErrorNegocio("El bien ya está en el carrito");
        }
        ItemCarrito item = new ItemCarrito();
        item.setSubasta(subasta);
        carrito.agregar(item);
        repoCarrito.save(carrito);
    }

    @Transactional(readOnly = true)
    public List<ItemCarritoDTO> listar(String cedula) {
        return repoCarrito.findByClienteCedula(cedula)
                .map(c -> c.getItems().stream()
                        .map(i -> new ItemCarritoDTO(i.getIdItem(), i.getSubasta().getIdSubasta(),
                                i.getSubasta().getBien().getNombre(), i.getSubasta().getBien().getValorMinimo(),
                                i.getSubasta().getFechaCierre()))
                        .toList())
                .orElse(List.of());
    }

    @Transactional
    public void quitar(String cedula, Long idItem) {
        repoCarrito.findByClienteCedula(cedula).ifPresent(c -> c.getItems().removeIf(i -> i.getIdItem().equals(idItem)));
    }

    @Transactional
    public void quitarSubastas(String cedula, List<Long> idsSubasta) {
        repoCarrito.findByClienteCedula(cedula)
                .ifPresent(c -> c.getItems().removeIf(i -> idsSubasta.contains(i.getSubasta().getIdSubasta())));
    }

    private CarritoSubasta nuevoCarrito(String cedula) {
        Cliente cliente = repoCliente.findById(cedula)
                .orElseThrow(() -> new ErrorNegocio("Cliente no registrado"));
        CarritoSubasta carrito = new CarritoSubasta();
        carrito.setCliente(cliente);
        return carrito;
    }
}
