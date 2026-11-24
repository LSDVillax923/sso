package co.edu.javeriana.ofac.entrada;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Boundary de entrada de OFAC: recibe la consulta del SSO, la marca y la entrega a WSValidarCliente (controlador). */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class APIRestOFAC extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(APIRestOFAC.class);

    @Override
    protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        respuesta.setHeader("X-Atendido-Por", "ofac-service");
        log.info("{} {} desde {}", peticion.getMethod(), peticion.getRequestURI(), peticion.getRemoteAddr());
        cadena.doFilter(peticion, respuesta);
    }
}
