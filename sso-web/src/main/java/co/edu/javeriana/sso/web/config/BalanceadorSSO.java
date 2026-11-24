package co.edu.javeriana.sso.web.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Balanceo del monolito SSO. Las réplicas se declaran en application.yml
 * (spring.cloud.discovery.client.simple.instances.sso-monolito) y se descartan las que no
 * responden /actuator/health. Para escalar basta con agregar otra réplica a esa lista.
 */
@Configuration
@LoadBalancerClient(name = "sso-monolito", configuration = BalanceadorSSO.Instancias.class)
public class BalanceadorSSO {

    static class Instancias {

        @Bean
        public ServiceInstanceListSupplier instancias(ConfigurableApplicationContext contexto) {
            return ServiceInstanceListSupplier.builder()
                    .withDiscoveryClient()
                    .withHealthChecks()
                    .build(contexto);
        }
    }
}
