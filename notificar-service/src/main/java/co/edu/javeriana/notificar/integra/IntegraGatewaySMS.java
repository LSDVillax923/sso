package co.edu.javeriana.notificar.integra;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Sale al gateway SMS por HTTPS. Sin URL configurada funciona como simulador y deja el SMS en el log. */
@Component
public class IntegraGatewaySMS {

    private static final Logger log = LoggerFactory.getLogger(IntegraGatewaySMS.class);

    private final RestClient restClient;
    private final String urlGateway;
    private final String apiKey;

    public IntegraGatewaySMS(RestClient.Builder builder, @Value("${notificar.sms.url-gateway:}") String urlGateway,
                             @Value("${notificar.sms.api-key:}") String apiKey) {
        this.restClient = builder.build();
        this.urlGateway = urlGateway;
        this.apiKey = apiKey;
    }

    public boolean enviarSMS(String celular, String texto) {
        if (urlGateway == null || urlGateway.isBlank()) {
            log.info("[SMS SIMULADO] a {}: {}", celular, texto);
            return true;
        }
        try {
            restClient.post().uri(urlGateway)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("to", celular, "message", texto))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            log.warn("Gateway SMS no disponible: {}", e.getMessage());
            return false;
        }
    }
}
