package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.compras.CompraDTO;
import co.edu.javeriana.sso.compras.RespuestaCompraDTO;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.entidad.EstadoOferta;
import co.edu.javeriana.sso.dominio.entidad.Oferta;
import co.edu.javeriana.sso.dominio.error.ErrorNegocio;
import co.edu.javeriana.sso.dominio.repo.RepoCompra;
import co.edu.javeriana.sso.dominio.repo.RepoOferta;
import co.edu.javeriana.sso.dominio.mensaje.MsgVerificarOFAC;
import co.edu.javeriana.sso.notificacion.PublicadorEventos;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvCompra {

    private final RepoOferta repoOferta;
    private final RepoCompra repoCompra;
    private final PublicadorEventos publicadorEventos;
    private final BigDecimal iva;
    private final int esperaMin;

    public SrvCompra(RepoOferta repoOferta, RepoCompra repoCompra, PublicadorEventos publicadorEventos,
                     @Value("${sso.impuestos.iva:0.19}") BigDecimal iva, @Value("${sso.ofac.espera-min:5}") int esperaMin) {
        this.repoOferta = repoOferta;
        this.repoCompra = repoCompra;
        this.publicadorEventos = publicadorEventos;
        this.iva = iva;
        this.esperaMin = esperaMin;
    }

    @Transactional
    public RespuestaCompraDTO iniciarCompra(String cedula, Long idOferta) {
        if (idOferta == null) {
            throw new ErrorNegocio(HttpStatus.BAD_REQUEST, "Falta el número de la oferta");
        }
        Oferta oferta = repoOferta.findById(idOferta)
                .orElseThrow(() -> new ErrorNegocio(HttpStatus.NOT_FOUND, "La oferta no existe"));
        if (!oferta.getCliente().getCedula().equals(cedula) || oferta.getEstado() != EstadoOferta.GANADORA) {
            throw new ErrorNegocio(HttpStatus.CONFLICT, "Esta oferta no fue ganada por usted");
        }
        if (repoCompra.existsByOferta(oferta)) {
            throw new ErrorNegocio(HttpStatus.CONFLICT, "Ya inició la compra de este bien");
        }
        BigDecimal total = calcularValorTotal(oferta);
        Compra compra = new Compra();
        compra.setOferta(oferta);
        compra.setCliente(oferta.getCliente());
        compra.setValorOferta(oferta.getValor());
        compra.setImpuestos(total.subtract(oferta.getValor()));
        compra.setValorTotal(total);
        compra.setEstado(EstadoCompra.VERIFICANDO_OFAC);
        compra.setIntentosOFAC(0);
        compra.setProximoIntento(LocalDateTime.now().plusMinutes(esperaMin));
        repoCompra.save(compra);
        publicadorEventos.publicarVerificacion(new MsgVerificarOFAC(compra.getNumeroCompra(), cedula));
        return new RespuestaCompraDTO(compra.getNumeroCompra(), compra.getEstado(),
                "Estamos validando su identidad. Le llegará un SMS cuando pueda pagar.");
    }

    /** Valor que ofreció en la subasta más impuestos. */
    public BigDecimal calcularValorTotal(Oferta oferta) {
        return oferta.getValor().multiply(BigDecimal.ONE.add(iva)).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public CompraDTO consultar(String cedula, Long numeroCompra) {
        Compra c = repoCompra.findByNumeroCompra(numeroCompra)
                .filter(x -> x.getCliente().getCedula().equals(cedula))
                .orElseThrow(() -> new ErrorNegocio(HttpStatus.NOT_FOUND, "La compra no existe"));
        return new CompraDTO(c.getNumeroCompra(), c.getOferta().getSubasta().getBien().getNombre(), c.getValorOferta(),
                c.getImpuestos(), c.getValorTotal(), c.getEstado());
    }
}
