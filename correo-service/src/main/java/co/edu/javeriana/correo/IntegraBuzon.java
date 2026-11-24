package co.edu.javeriana.correo;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.HtmlUtils;

/** Salida al buzón del cliente: guarda en bd_correo y le muestra sus correos en el navegador (en lugar de IMAP). */
@Controller
public class IntegraBuzon {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");

    private final JdbcClient jdbc;

    public IntegraBuzon(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void guardar(String para, String asunto, String cuerpo) {
        jdbc.sql("insert into correo (para, asunto, cuerpo, fecha) values (?, ?, ?, ?)")
                .params(para, asunto, cuerpo, LocalDateTime.now())
                .update();
    }

    @GetMapping("/buzon/{direccion}")
    public void mostrar(@PathVariable String direccion, HttpServletResponse respuesta) throws IOException {
        // cada correo: {fecha, asunto, cuerpo}
        List<String[]> correos = jdbc.sql("select asunto, cuerpo, fecha from correo where para = ? order by fecha desc")
                .param(direccion.toLowerCase())
                .query((rs, n) -> new String[] {rs.getTimestamp("fecha").toLocalDateTime().format(FECHA),
                        rs.getString("asunto"), rs.getString("cuerpo")})
                .list();
        respuesta.setContentType("text/html;charset=UTF-8");
        PrintWriter html = respuesta.getWriter();
        html.println("<!doctype html><html lang=\"es\"><head><meta charset=\"utf-8\"><title>Buzón de " + HtmlUtils.htmlEscape(direccion, "UTF-8") + "</title>");
        html.println("<style>body{font-family:system-ui,sans-serif;max-width:760px;margin:24px auto;padding:0 16px;color:#1d2433}"
                + "article{border:1px solid #d8dee8;border-radius:10px;padding:16px;margin:12px 0}small{color:#5b6578}</style></head><body>");
        html.println("<h1>Buzón de " + HtmlUtils.htmlEscape(direccion, "UTF-8") + "</h1>");
        if (correos.isEmpty()) {
            html.println("<p>No hay correos.</p>");
        }
        for (String[] c : correos) {
            // el cuerpo es el HTML que mandó el SSO, con el enlace para iniciar la compra
            html.println("<article><small>" + c[0] + "</small><h2>" + HtmlUtils.htmlEscape(c[1], "UTF-8") + "</h2>" + c[2] + "</article>");
        }
        html.println("</body></html>");
    }
}
