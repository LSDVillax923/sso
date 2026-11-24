package co.edu.javeriana.correo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"correo.puerto=12587", "spring.datasource.url=jdbc:h2:mem:bd_correo"})
class ListenerSMTPTest {

    @Autowired
    private TestRestTemplate web;

    @Test
    void recibeElCorreoDeIntegraSMTPYLoMuestraEnElBuzon() throws Exception {
        // misma configuración que notificar-service usa contra Gmail
        JavaMailSenderImpl smtp = new JavaMailSenderImpl();
        smtp.setHost("localhost");
        smtp.setPort(12587);
        smtp.setUsername("sso@gmail.com");
        smtp.setPassword("clave-de-aplicacion");
        Properties p = smtp.getJavaMailProperties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");

        MimeMessage mensaje = smtp.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
        helper.setFrom("no-responder@sso.local");
        helper.setTo("Ana@Correo.com");
        helper.setSubject("SSO: ganó la subasta de Camión");
        helper.setText("<p>Hola Ana,</p><p>Para continuar, entre a <a href=\"https://192.168.1.101:8443/mis-subastas\">SSO</a>.</p>", true);
        smtp.send(mensaje);

        String buzon = web.getForObject("/buzon/ana@correo.com", String.class);
        assertTrue(buzon.contains("ganó la subasta de Camión"), buzon);
        assertTrue(buzon.contains("/mis-subastas"), buzon);
    }
}
