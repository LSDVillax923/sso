package co.edu.javeriana.sso.dominio.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class ConfigWebClient {

    /** Timeout para OFAC: una caída de red no deja el hilo colgado (L07 s25) */
    @Bean
    WebClient.Builder webClientBuilder() {
        HttpClient reactorHttpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                .responseTimeout(Duration.ofSeconds(5));
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(reactorHttpClient));
    }
}
