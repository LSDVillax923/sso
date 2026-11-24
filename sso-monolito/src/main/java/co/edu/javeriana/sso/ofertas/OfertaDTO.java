package co.edu.javeriana.sso.ofertas;

import co.edu.javeriana.sso.dominio.entidad.EstadoOferta;
import java.math.BigDecimal;

public record OfertaDTO(Long idOferta, Long idSubasta, String nombreBien, BigDecimal valor, EstadoOferta estado) {
}
