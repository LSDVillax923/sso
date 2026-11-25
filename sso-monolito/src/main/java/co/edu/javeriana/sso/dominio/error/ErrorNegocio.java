package co.edu.javeriana.sso.dominio.error;

/** Regla de negocio que no se cumple; ManejadorErrores la convierte en la respuesta HTTP. */
public class ErrorNegocio extends RuntimeException {

    public ErrorNegocio(String mensaje) {
        super(mensaje);
    }
}
