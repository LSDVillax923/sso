package co.edu.javeriana.sso;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Monolito del SSO. Corre en 2 réplicas en PC2 detrás de BalanceadorSSO (sso-web, PC1). */
@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableSchedulerLock(defaultLockAtMostFor = "PT4M")
public class SsoMonolitoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoMonolitoApplication.class, args);
    }
}
