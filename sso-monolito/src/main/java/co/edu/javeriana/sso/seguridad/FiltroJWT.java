package co.edu.javeriana.sso.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Si llega un JWT válido deja la cédula como usuario autenticado; los WS la reciben con @AuthenticationPrincipal. */
@Component
public class FiltroJWT extends OncePerRequestFilter {

    private final UtilJWT utilJWT;

    public FiltroJWT(UtilJWT utilJWT) {
        this.utilJWT = utilJWT;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String cabecera = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            String token = cabecera.substring(7);
            String cedula = utilJWT.obtenerCedula(token);
            if (cedula != null) {
                var autenticacion = new UsernamePasswordAuthenticationToken(cedula, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            }
        }
        chain.doFilter(req, res);
    }
}
