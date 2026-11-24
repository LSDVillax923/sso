package co.edu.javeriana.sso.seguridad;

import co.edu.javeriana.sso.dominio.entidad.Cliente;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UtilJWT {

    private final SecretKey clave;
    private final int expiracionMin;

    public UtilJWT(@Value("${sso.jwt.secreto}") String secreto, @Value("${sso.jwt.expiracion-min:60}") int expiracionMin) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMin = expiracionMin;
    }

    public String generarToken(Cliente cliente) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .subject(cliente.getCedula())
                .claim("nombre", cliente.getNombre())
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + expiracionMin * 60_000L))
                .signWith(clave)
                .compact();
    }

    public boolean validar(String token) {
        try {
            Jwts.parser().verifyWith(clave).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String obtenerCedula(String token) {
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
