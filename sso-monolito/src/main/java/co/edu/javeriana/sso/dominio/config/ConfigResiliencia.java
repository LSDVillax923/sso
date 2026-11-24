package co.edu.javeriana.sso.dominio.config;

import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClient;

@Configuration
public class ConfigResiliencia {

    /** Timeout para OFAC y YoPago: una caída de red no deja el hilo colgado. */
    @Bean
    public RestClient restClient(RestClient.Builder builder, @Value("${sso.http.timeout-ms:5000}") int timeoutMs) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(timeoutMs);
        fabrica.setReadTimeout(timeoutMs);
        return builder.requestFactory(fabrica).build();
    }

    /** Los timers usan la tabla shedlock de bd_sso para no correr en las dos réplicas a la vez. */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .usingDbTime()
                .build());
    }
}
