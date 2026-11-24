package co.edu.javeriana.sso.catalogo;

import co.edu.javeriana.sso.catalogo.ItemCarritoDTO;
import co.edu.javeriana.sso.dominio.entidad.CarritoSubasta;
import co.edu.javeriana.sso.dominio.entidad.Cliente;
import co.edu.javeriana.sso.dominio.entidad.ItemCarrito;
import co.edu.javeriana.sso.dominio.entidad.Subasta;
import co.edu.javeriana.sso.dominio.error.ErrorNegocio;
import co.edu.javeriana.sso.dominio.integra.IntegraBDCarrito;
import co.edu.javeriana.sso.dominio.integra.IntegraBDCliente;
import co.edu.javeriana.sso.dominio.integra.IntegraBDSubasta;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvCarrito {

    private final IntegraBDCarrito integraBDCarrito;
    private final IntegraBDSubasta integraBDSubasta;
    private final IntegraBDCliente integraBDCliente;

    public SrvCarrito(IntegraBDCarrito integraBDCarrito, IntegraBDSubasta integraBDSubasta, IntegraBDCliente integraBDCliente) {
        this.integraBDCarrito = integraBDCarrito;
        this.integraBDSubasta = integraBDSubasta;
        this.integraBDCliente = integraBDCliente;
    }

    @Transactional
    public void agregarItem(String cedula, Long idSubasta) {
        Subasta subasta = integraBDSubasta.findById(idSubasta)
                .orElseThrow(() -> new ErrorNegocio(HttpStatus.NOT_FOUND, "La subasta no existe"));
        if (!subasta.estaAbierta(LocalDateTime.now())) {
            throw new ErrorNegocio(HttpStatus.CONFLICT, "La subasta no está abierta");
        }
        CarritoSubasta carrito = integraBDCarrito.findByClienteCedula(cedula).orElseGet(() -> nuevoCarrito(cedula));
        boolean repetido = carrito.getItems().stream().anyMatch(i -> i.getSubasta().getIdSubasta().equals(idSubasta));
        if (repetido) {
            throw new ErrorNegocio(HttpStatus.CONFLICT, "El bien ya está en el carrito");
        }
        ItemCarrito item = new ItemCarrito();
        item.setSubasta(subasta);
        item.setFechaAgregado(LocalDateTime.now());
        carrito.agregar(item);
        integraBDCarrito.save(carrito);
    }

    @Transactional(readOnly = true)
    public List<ItemCarritoDTO> listar(String cedula) {
        return integraBDCarrito.findByClienteCedula(cedula)
                .map(c -> c.getItems().stream()
                        .map(i -> new ItemCarritoDTO(i.getIdItem(), i.getSubasta().getIdSubasta(),
                                i.getSubasta().getBien().getNombre(), i.getSubasta().getBien().getValorMinimo(),
                                i.getSubasta().getFechaCierre()))
                        .toList())
                .orElse(List.of());
    }

    @Transactional
    public void quitar(String cedula, Long idItem) {
        integraBDCarrito.findByClienteCedula(cedula).ifPresent(c -> c.getItems().removeIf(i -> i.getIdItem().equals(idItem)));
    }

    @Transactional
    public void quitarSubastas(String cedula, List<Long> idsSubasta) {
        integraBDCarrito.findByClienteCedula(cedula)
                .ifPresent(c -> c.getItems().removeIf(i -> idsSubasta.contains(i.getSubasta().getIdSubasta())));
    }

    private CarritoSubasta nuevoCarrito(String cedula) {
        Cliente cliente = integraBDCliente.findById(cedula)
                .orElseThrow(() -> new ErrorNegocio(HttpStatus.FORBIDDEN, "Cliente no registrado"));
        CarritoSubasta carrito = new CarritoSubasta();
        carrito.setCliente(cliente);
        return carrito;
    }
}
