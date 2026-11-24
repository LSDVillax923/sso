package co.edu.javeriana.sso.dominio.error;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ErrorNegocio.class)
    public ResponseEntity<Map<String, String>> negocio(ErrorNegocio e) {
        return ResponseEntity.status(e.getEstado()).body(Map.of("mensaje", e.getMessage()));
    }
}
