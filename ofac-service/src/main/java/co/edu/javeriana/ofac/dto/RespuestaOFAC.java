package co.edu.javeriana.ofac.dto;

import java.time.LocalDateTime;

public record RespuestaOFAC(String cedula, ResultadoOFAC estado, LocalDateTime fechaConsulta) {
}
