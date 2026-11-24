package co.edu.javeriana.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.datasource.url=jdbc:h2:mem:bd_sms", "gateway.api-key=clave-prueba"})
class ListenerGatewaySMSTest {

    @Autowired
    private TestRestTemplate web;

    @Autowired
    private JdbcClient jdbc;

    /** Lo mismo que manda IntegraGatewaySMS de notificar-service. */
    private ResponseEntity<Map> enviar(String celular, String texto, String apiKey) {
        HttpHeaders cabeceras = new HttpHeaders();
        cabeceras.setBearerAuth(apiKey);
        return web.postForEntity("/api/sms", new HttpEntity<>(Map.of("to", celular, "message", texto), cabeceras), Map.class);
    }

    @Test
    void entregaElSmsYLoRegistraEnBdSms() {
        ResponseEntity<Map> r = enviar("3001234567", "SSO: su compra 7 fue aprobada. Pague en https://192.168.1.102:5001", "clave-prueba");
        assertEquals(HttpStatus.ACCEPTED, r.getStatusCode());
        assertEquals(1, jdbc.sql("select count(*) from sms where numero = '3001234567' and estado = 'ENTREGADO'").query(Integer.class).single());
    }

    @Test
    void reintentaTresVecesYQuedaFallidoSiElCelularNoExiste() {
        ResponseEntity<Map> r = enviar("12345", "SSO: prueba", "clave-prueba");
        assertEquals(HttpStatus.ACCEPTED, r.getStatusCode());
        assertEquals(1, jdbc.sql("select count(*) from sms where numero = '12345' and estado = 'FALLIDO'").query(Integer.class).single());
    }

    @Test
    void rechazaUnaApiKeyEquivocada() {
        assertEquals(HttpStatus.UNAUTHORIZED, enviar("3001234567", "hola", "otra").getStatusCode());
    }
}
