package co.edu.javeriana.sso.ofertas;

import java.util.List;
import java.util.Map;

public record RespuestaOfertaDTO(String mensaje, List<Long> aceptadas, Map<Long, String> rechazadas) {
}
