package co.edu.javeriana.sms;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/** Salida al operador móvil (el celular del cliente) y a bd_sms. El operador se simula: el SMS se ve en el log. */
@Component
public class IntegraOperadorMovil {

    private static final Logger log = LoggerFactory.getLogger(IntegraOperadorMovil.class);

    private final JdbcClient jdbc;

    public IntegraOperadorMovil(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void entregar(String numero, String texto) {
        String celular = numero.replaceAll("[^0-9]", "");
        if (celular.startsWith("57") && celular.length() == 12) {
            celular = celular.substring(2);
        }
        // celulares de Colombia: 10 dígitos que empiezan por 3
        if (!celular.matches("3\\d{9}")) {
            throw new IllegalArgumentException("el operador no reconoce el celular " + numero);
        }
        log.info("SMS entregado al celular {}:\n    {}", celular, texto);
    }

    public void registrar(String numero, String texto, String estado) {
        jdbc.sql("insert into sms (numero, texto, estado, fecha) values (?, ?, ?, ?)")
                .params(numero, texto, estado, LocalDateTime.now())
                .update();
    }
}
