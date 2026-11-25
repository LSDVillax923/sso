package co.edu.javeriana.notificar.integra;

import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** Sale al gateway SMS por HTTPS. Sin URL configurada funciona como simulador y deja el SMS en el log. */
@Component
public class IntegraGatewaySMS {

    private static final Logger log = LoggerFactory.getLogger(IntegraGatewaySMS.class);

    private final WebClient webClient;
    private final String urlGateway;
    private final String apiKey;

    public IntegraGatewaySMS(WebClient.Builder builder, @Value("${notificar.sms.url-gateway:}") String urlGateway,
                             @Value("${notificar.sms.api-key:}") String apiKey) {
        this.webClient = builder.build();
        this.urlGateway = urlGateway;
        this.apiKey = apiKey;
    }

    public boolean enviarSMS(String celular, String texto) {
        if (urlGateway == null || urlGateway.isBlank()) {
            log.info("[SMS SIMULADO] a {}: {}", celular, texto);
            return true;
        }
        return webClient.post().uri(urlGateway)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("to", celular, "message", texto))
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .map(r -> true)
                .onErrorResume(e -> {
                    log.warn("Gateway SMS no disponible: {}", e.getMessage());
                    return Mono.just(false);
                })
                .block();
    }
}
