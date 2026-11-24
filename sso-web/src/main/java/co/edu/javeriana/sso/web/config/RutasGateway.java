package co.edu.javeriana.sso.web.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Única puerta de entrada desde Internet: el SPA Angular pasa por aquí hacia el monolito. */
@Configuration
public class RutasGateway {

    @Bean
    public RouteLocator rutas(RouteLocatorBuilder builder) {
        return builder.routes()
                // lb:// usa BalanceadorSSO: round-robin entre las réplicas de sso-monolito
                .route("sso-monolito", r -> r.path("/api/**").filters(f -> f.retry(2)).uri("lb://sso-monolito"))
                .build();
    }
}
