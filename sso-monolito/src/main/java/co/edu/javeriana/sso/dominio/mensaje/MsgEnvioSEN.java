package co.edu.javeriana.sso.dominio.mensaje;

public record MsgEnvioSEN(Long numeroCompra, Long idBien, String descripcion, String cedula, String nombre,
                          String direccion, String celular) {
}
