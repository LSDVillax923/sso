package co.edu.javeriana.sso.dominio.mensaje;

import co.edu.javeriana.sso.dominio.entidad.SistemaDestino;
import java.time.LocalDateTime;

public record MsgAck(Long numeroCompra, SistemaDestino sistema, LocalDateTime fecha) {
}
