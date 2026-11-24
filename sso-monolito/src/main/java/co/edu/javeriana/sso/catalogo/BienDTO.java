package co.edu.javeriana.sso.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BienDTO(Long idSubasta, Long idBien, String nombre, String descripcion, BigDecimal valorMinimo,
                      LocalDateTime fechaCierre) {
}
