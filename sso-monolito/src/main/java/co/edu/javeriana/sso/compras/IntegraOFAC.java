package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.compras.EstadoOFAC;
import co.edu.javeriana.sso.compras.RespuestaOFACDTO;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/** Consulta la OFAC con WebClient: timeout de 5 s y 3 reintentos; si no responde, la compra se trata como ENPROCESO (L07). */
@Component
public class IntegraOFAC {

    private static final Logger log = LoggerFactory.getLogger(IntegraOFAC.class);

    private final WebClient webClient;
    private final String urlOFAC;

    public IntegraOFAC(WebClient.Builder builder, @Value("${sso.ofac.url}") String urlOFAC) {
        this.webClient = builder.build();
        this.urlOFAC = urlOFAC;
    }

    public RespuestaOFACDTO consultar(String cedula) {
        return webClient.get().uri(urlOFAC + "{cedula}", cedula)
                .retrieve()
                .bodyToMono(RespuestaOFACDTO.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .onErrorResume(e -> {
                    log.warn("OFAC no responde para la cédula {}: {}", cedula, e.getMessage());
                    return Mono.just(new RespuestaOFACDTO(cedula, EstadoOFAC.ENPROCESO));
                })
                .block();
    }
}
