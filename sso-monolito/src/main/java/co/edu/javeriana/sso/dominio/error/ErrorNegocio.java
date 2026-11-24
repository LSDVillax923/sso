package co.edu.javeriana.sso.dominio.error;

import org.springframework.http.HttpStatus;

/** Regla de negocio que no se cumple; ManejadorErrores la convierte en la respuesta HTTP. */
public class ErrorNegocio extends RuntimeException {

    private final HttpStatus estado;

    public ErrorNegocio(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}
