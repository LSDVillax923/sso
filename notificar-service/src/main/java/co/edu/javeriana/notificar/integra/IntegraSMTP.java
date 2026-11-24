package co.edu.javeriana.notificar.integra;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/** Sale al servidor de correo por SMTP 587. En modo simulado deja el correo en el log. */
@Component
public class IntegraSMTP {

    private static final Logger log = LoggerFactory.getLogger(IntegraSMTP.class);

    private final JavaMailSender mailSender;
    private final String remitente;
    private final boolean simulado;

    public IntegraSMTP(JavaMailSender mailSender, @Value("${notificar.correo.remitente}") String remitente,
                       @Value("${notificar.correo.simulado:true}") boolean simulado) {
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.simulado = simulado;
    }

    public void enviarCorreo(String para, String asunto, String html) {
        if (simulado) {
            log.info("[CORREO SIMULADO] para={} asunto={}\n{}", para, asunto, html);
            return;
        }
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new IllegalStateException("No se pudo armar el correo para " + para, e);
        }
    }
}
