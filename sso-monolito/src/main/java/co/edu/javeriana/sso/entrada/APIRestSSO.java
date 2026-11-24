package co.edu.javeriana.sso.entrada;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Boundary de entrada del monolito: toda petición HTTP que manda el BalanceadorSSO pasa primero por aquí,
 * se marca con la réplica que la atiende (cabecera X-Atendido-Por) y sigue hacia el WS, que es un controlador.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class APIRestSSO extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(APIRestSSO.class);

    private final String replica;

    public APIRestSSO(@Value("${sso.replica:sso-monolito-1}") String replica) {
        this.replica = replica;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        respuesta.setHeader("X-Atendido-Por", replica);
        log.info("[{}] {} {}", replica, peticion.getMethod(), peticion.getRequestURI());
        cadena.doFilter(peticion, respuesta);
    }
}
