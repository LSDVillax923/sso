package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.compras.EstadoOFAC;
import co.edu.javeriana.sso.compras.RespuestaOFACDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Consulta la OFAC con timeout y 3 intentos; si no responde, la compra se trata como ENPROCESO. */
@Component
public class IntegraOFAC {

    private static final Logger log = LoggerFactory.getLogger(IntegraOFAC.class);

    private final RestClient restClient;
    private final String urlOFAC;

    public IntegraOFAC(RestClient restClient, @Value("${sso.ofac.url}") String urlOFAC) {
        this.restClient = restClient;
        this.urlOFAC = urlOFAC;
    }

    @Retryable(retryFor = RestClientException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public RespuestaOFACDTO consultar(String cedula) {
        return restClient.get().uri(urlOFAC + "{cedula}", cedula).retrieve().body(RespuestaOFACDTO.class);
    }

    @Recover
    public RespuestaOFACDTO sinRespuesta(RestClientException e, String cedula) {
        log.warn("OFAC no responde para la cédula {}: {}", cedula, e.getMessage());
        return new RespuestaOFACDTO(cedula, EstadoOFAC.ENPROCESO);
    }
}
