package co.edu.javeriana.sms;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/** API del proveedor: recibe el POST {"to", "message"} que manda IntegraGatewaySMS y lo pasa a SRBDespacharSMS. */
@RestController
public class LisenerGatewaySMS {

    private final SRBDespacharSMS srbDespacharSMS;
    private final String apiKey;

    public LisenerGatewaySMS(SRBDespacharSMS srbDespacharSMS, @Value("${gateway.api-key:}") String apiKey) {
        this.srbDespacharSMS = srbDespacharSMS;
        this.apiKey = apiKey;
    }

    @PostMapping("/api/sms")
    public ResponseEntity<Map<String, String>> recibir(@RequestBody Map<String, String> sms,
                                                       @RequestHeader(value = "Authorization", required = false) String autorizacion) {
        if (!apiKey.isBlank() && !("Bearer " + apiKey).equals(autorizacion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "API key inválida"));
        }
        String numero = sms.get("to");
        String texto = sms.get("message");
        if (numero == null || texto == null || texto.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "faltan to o message"));
        }
        srbDespacharSMS.despachar(numero, texto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("estado", "ACEPTADO"));
    }
}
