package co.edu.javeriana.sso.web.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** Las rutas del Angular se resuelven en el navegador: el servidor siempre devuelve index.html. */
@Controller
public class ControladorSPA {

    private final Resource index = new ClassPathResource("static/index.html");

    @GetMapping(value = {"/login", "/catalogo", "/carrito", "/oferta", "/mis-subastas", "/compra/{numero}"},
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public Resource index() {
        return index;
    }
}
