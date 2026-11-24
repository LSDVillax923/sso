package co.edu.javeriana.sso.ofertas;

import co.edu.javeriana.sso.catalogo.SrvCarrito;
import co.edu.javeriana.sso.dominio.entidad.Cliente;
import co.edu.javeriana.sso.dominio.entidad.EstadoOferta;
import co.edu.javeriana.sso.dominio.entidad.Oferta;
import co.edu.javeriana.sso.dominio.entidad.Subasta;
import co.edu.javeriana.sso.dominio.error.ErrorNegocio;
import co.edu.javeriana.sso.dominio.integra.IntegraBDCliente;
import co.edu.javeriana.sso.dominio.integra.IntegraBDOferta;
import co.edu.javeriana.sso.dominio.integra.IntegraBDSubasta;
import co.edu.javeriana.sso.ofertas.ItemOfertaDTO;
import co.edu.javeriana.sso.ofertas.OfertaDTO;
import co.edu.javeriana.sso.ofertas.PeticionOfertaDTO;
import co.edu.javeriana.sso.ofertas.RespuestaOfertaDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvOferta {

    private static final String MENSAJE_OK =
            "Su oferta fue registrada. Le llegará un mensaje al celular cuando sea posible continuar la compra.";

    private final IntegraBDSubasta integraBDSubasta;
    private final IntegraBDOferta integraBDOferta;
    private final IntegraBDCliente integraBDCliente;
    private final SrvCarrito srvCarrito;

    public SrvOferta(IntegraBDSubasta integraBDSubasta, IntegraBDOferta integraBDOferta, IntegraBDCliente integraBDCliente, SrvCarrito srvCarrito) {
        this.integraBDSubasta = integraBDSubasta;
        this.integraBDOferta = integraBDOferta;
        this.integraBDCliente = integraBDCliente;
        this.srvCarrito = srvCarrito;
    }

    @Transactional
    public RespuestaOfertaDTO registrarOfertas(String cedula, PeticionOfertaDTO peticion) {
        if (peticion == null || peticion.items() == null || peticion.items().isEmpty()) {
            throw new ErrorNegocio(HttpStatus.BAD_REQUEST, "La petición no trae bienes para ofertar");
        }
        Cliente cliente = integraBDCliente.findById(cedula)
                .orElseThrow(() -> new ErrorNegocio(HttpStatus.FORBIDDEN, "Cliente no registrado"));
        List<Long> aceptadas = new ArrayList<>();
        Map<Long, String> rechazadas = new LinkedHashMap<>();
        for (ItemOfertaDTO item : peticion.items()) {
            Optional<Subasta> subasta = integraBDSubasta.findById(item.idSubasta());
            if (subasta.isEmpty()) {
                rechazadas.put(item.idSubasta(), "La subasta no existe");
                continue;
            }
            if (!validarOferta(subasta.get(), item.valor())) {
                rechazadas.put(item.idSubasta(), motivoRechazo(subasta.get(), item.valor()));
                continue;
            }
            Oferta oferta = new Oferta();
            oferta.setCliente(cliente);
            oferta.setSubasta(subasta.get());
            oferta.setValor(item.valor());
            oferta.setFechaHora(LocalDateTime.now());
            oferta.setEstado(EstadoOferta.REGISTRADA);
            integraBDOferta.save(oferta);
            aceptadas.add(item.idSubasta());
        }
        srvCarrito.vaciar(cedula);
        String mensaje = aceptadas.isEmpty() ? "Ninguna oferta fue registrada. Revise los motivos." : MENSAJE_OK;
        return new RespuestaOfertaDTO(mensaje, aceptadas, rechazadas);
    }

    @Transactional(readOnly = true)
    public List<OfertaDTO> consultarGanadoras(String cedula) {
        return integraBDOferta.findByClienteCedulaAndEstado(cedula, EstadoOferta.GANADORA).stream()
                .map(o -> new OfertaDTO(o.getIdOferta(), o.getSubasta().getIdSubasta(),
                        o.getSubasta().getBien().getNombre(), o.getValor(), o.getEstado()))
                .toList();
    }

    /** Regla del enunciado: entre fecha de inicio y cierre, y valor mayor o igual al mínimo. */
    private boolean validarOferta(Subasta subasta, BigDecimal valor) {
        return motivoRechazo(subasta, valor) == null;
    }

    private String motivoRechazo(Subasta subasta, BigDecimal valor) {
        if (!subasta.estaAbierta(LocalDateTime.now())) {
            return "La subasta no está recibiendo ofertas";
        }
        if (valor == null || valor.compareTo(subasta.getBien().getValorMinimo()) < 0) {
            return "El valor es menor al mínimo de " + subasta.getBien().getValorMinimo().toPlainString();
        }
        return null;
    }
}
