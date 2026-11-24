package co.edu.javeriana.correo;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Escucha SMTP: atiende la sesión que abre IntegraSMTP de notificar-service (EHLO, AUTH, MAIL FROM, RCPT TO, DATA)
 * y pasa cada correo a SRBEntregarCorreo. No ofrece STARTTLS, así que JavaMail sigue sin TLS.
 */
@Component
public class LisenerSMTP implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(LisenerSMTP.class);

    private final int puerto;
    private final SRBEntregarCorreo srbEntregarCorreo;
    private final ExecutorService sesiones = Executors.newVirtualThreadPerTaskExecutor();
    private volatile ServerSocket servidor;

    public LisenerSMTP(@Value("${correo.puerto:587}") int puerto, SRBEntregarCorreo srbEntregarCorreo) {
        this.puerto = puerto;
        this.srbEntregarCorreo = srbEntregarCorreo;
    }

    public void recibir(String de, String para, String asunto, String cuerpo) {
        log.info("Correo de {} para {}: {}", de, para, asunto);
        srbEntregarCorreo.entregar(para, asunto, cuerpo);
    }

    private void atender(Socket socket) {
        try (socket;
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1));
             Writer salida = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.ISO_8859_1)) {
            responder(salida, "220 correo.sso.local ESMTP simulado");
            String de = null;
            List<String> para = new ArrayList<>();
            for (String linea = entrada.readLine(); linea != null; linea = entrada.readLine()) {
                String comando = linea.toUpperCase();
                if (comando.startsWith("EHLO")) {
                    responder(salida, "250-correo.sso.local\r\n250-AUTH PLAIN LOGIN\r\n250-8BITMIME\r\n250 SIZE 10485760");
                } else if (comando.startsWith("HELO")) {
                    responder(salida, "250 correo.sso.local");
                } else if (comando.startsWith("AUTH PLAIN")) {
                    if (linea.trim().length() <= "AUTH PLAIN".length()) {
                        responder(salida, "334 ");
                        entrada.readLine();
                    }
                    responder(salida, "235 2.7.0 autenticado");
                } else if (comando.startsWith("AUTH LOGIN")) {
                    responder(salida, "334 VXNlcm5hbWU6");
                    entrada.readLine();
                    responder(salida, "334 UGFzc3dvcmQ6");
                    entrada.readLine();
                    responder(salida, "235 2.7.0 autenticado");
                } else if (comando.startsWith("MAIL FROM:")) {
                    de = direccion(linea);
                    para.clear();
                    responder(salida, "250 2.1.0 OK");
                } else if (comando.startsWith("RCPT TO:")) {
                    para.add(direccion(linea));
                    responder(salida, "250 2.1.5 OK");
                } else if (comando.equals("DATA")) {
                    responder(salida, "354 termine con <CRLF>.<CRLF>");
                    StringBuilder mensaje = new StringBuilder();
                    for (String l = entrada.readLine(); l != null && !l.equals("."); l = entrada.readLine()) {
                        mensaje.append(l.startsWith("..") ? l.substring(1) : l).append("\r\n");
                    }
                    entregarMensaje(de, para, mensaje.toString().getBytes(StandardCharsets.ISO_8859_1), salida);
                    para.clear();
                } else if (comando.equals("RSET")) {
                    de = null;
                    para.clear();
                    responder(salida, "250 2.0.0 OK");
                } else if (comando.equals("NOOP")) {
                    responder(salida, "250 2.0.0 OK");
                } else if (comando.equals("QUIT")) {
                    responder(salida, "221 2.0.0 hasta luego");
                    return;
                } else {
                    responder(salida, "502 5.5.2 comando no soportado");
                }
            }
        } catch (IOException e) {
            log.warn("Sesión SMTP cortada: {}", e.getMessage());
        }
    }

    private void entregarMensaje(String de, List<String> para, byte[] crudo, Writer salida) throws IOException {
        try {
            MimeMessage mensaje = new MimeMessage(Session.getInstance(new Properties()), new ByteArrayInputStream(crudo));
            String cuerpo = texto(mensaje);
            for (String destinatario : para) {
                recibir(de, destinatario, mensaje.getSubject(), cuerpo);
            }
            responder(salida, "250 2.0.0 OK entregado");
        } catch (MessagingException | RuntimeException e) {
            log.error("No se pudo entregar el correo de {}", de, e);
            responder(salida, "554 5.6.0 no se pudo entregar: " + e.getMessage());
        }
    }

    /** El cuerpo HTML si lo hay; si no, el texto plano. */
    private static String texto(Part parte) throws MessagingException, IOException {
        if (parte.isMimeType("text/*")) {
            return (String) parte.getContent();
        }
        if (parte.isMimeType("multipart/*")) {
            Multipart multi = (Multipart) parte.getContent();
            String plano = null;
            for (int i = 0; i < multi.getCount(); i++) {
                BodyPart bp = multi.getBodyPart(i);
                String t = texto(bp);
                if (bp.isMimeType("text/html") || (t != null && t.contains("<"))) return t;
                if (plano == null) plano = t;
            }
            return plano;
        }
        return null;
    }

    private static String direccion(String linea) {
        String valor = linea.substring(linea.indexOf(':') + 1).trim();
        int a = valor.indexOf('<'), b = valor.indexOf('>');
        return (a >= 0 && b > a ? valor.substring(a + 1, b) : valor.split(" ")[0]).trim();
    }

    private static void responder(Writer salida, String texto) throws IOException {
        salida.write(texto + "\r\n");
        salida.flush();
    }

    /* ---------- ciclo de vida ---------- */

    @Override
    public void start() {
        try {
            servidor = new ServerSocket(puerto);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir SMTP en el puerto " + puerto, e);
        }
        Thread.ofPlatform().name("smtp-" + puerto).start(() -> {
            while (!servidor.isClosed()) {
                try {
                    Socket socket = servidor.accept();
                    sesiones.submit(() -> atender(socket));
                } catch (IOException e) {
                    if (!servidor.isClosed()) log.warn("Error aceptando SMTP: {}", e.getMessage());
                }
            }
        });
        log.info("Servidor de correo simulado escuchando SMTP en el puerto {}", puerto);
    }

    @Override
    public void stop() {
        try {
            if (servidor != null) servidor.close();
        } catch (IOException e) {
            log.debug("Cerrando SMTP", e);
        }
        sesiones.shutdownNow();
    }

    @Override
    public boolean isRunning() {
        return servidor != null && !servidor.isClosed();
    }
}
