package co.edu.javeriana.sso.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemCarritoDTO(Long idItem, Long idSubasta, String nombreBien, BigDecimal valorMinimo,
                             LocalDateTime fechaCierre) {
}
