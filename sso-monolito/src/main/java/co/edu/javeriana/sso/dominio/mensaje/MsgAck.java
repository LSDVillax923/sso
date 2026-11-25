package co.edu.javeriana.sso.dominio.mensaje;

import co.edu.javeriana.sso.dominio.entidad.SistemaDestino;

public record MsgAck(Long numeroCompra, SistemaDestino sistema) {
}
