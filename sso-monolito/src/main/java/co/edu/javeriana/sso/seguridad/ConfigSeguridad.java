package co.edu.javeriana.sso.seguridad;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Sin sesión: cualquier réplica atiende cualquier petición porque el JWT viaja en cada llamada. */
@Configuration
public class ConfigSeguridad {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, FiltroJWT filtroJWT) throws Exception {
        http.csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/auth/**", "/actuator/health", "/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .addFilterBefore(filtroJWT, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
