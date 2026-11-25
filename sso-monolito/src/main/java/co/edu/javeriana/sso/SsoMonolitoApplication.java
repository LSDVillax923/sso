package co.edu.javeriana.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Monolito del SSO. Corre en 2 réplicas en PC2 detrás de NGINX (PC1). */
@SpringBootApplication
@EnableScheduling
public class SsoMonolitoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoMonolitoApplication.class, args);
    }
}
