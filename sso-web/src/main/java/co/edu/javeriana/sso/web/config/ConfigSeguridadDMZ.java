package co.edu.javeriana.sso.web.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/** TLS se activa con server.ssl.* en application.yml; aquí solo se limita quién puede llamar la API desde el navegador. */
@Configuration
public class ConfigSeguridadDMZ {

    @Bean
    public CorsWebFilter configurarCors(@Value("${sso.origenes-permitidos}") List<String> origenes) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(origenes);
        cors.addAllowedMethod("*");
        cors.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", cors);
        return new CorsWebFilter(fuente);
    }
}
