package co.edu.javeriana.notificar.srb;

import co.edu.javeriana.notificar.dto.DTONotificacion;
import co.edu.javeriana.notificar.dto.PlantillaNotificacion;
import co.edu.javeriana.notificar.integra.IntegraSMTP;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Arma el correo de la plantilla y lo manda por IntegraSMTP. */
@Service
public class SRBEnviarCorreo {

    private final IntegraSMTP integraSMTP;

    public SRBEnviarCorreo(IntegraSMTP integraSMTP) {
        this.integraSMTP = integraSMTP;
    }

    public void enviar(DTONotificacion dto) {
        String asunto = dto.plantilla() == PlantillaNotificacion.GANADOR_SUBASTA
                ? reemplazar("SSO: ganó la subasta de {bien}", dto.datos())
                : "SSO: novedades de su compra";
        String cuerpo = armarCuerpo(dto.plantilla(), dto.datos());
        integraSMTP.enviarCorreo(dto.destino(), asunto, cuerpo);
    }

    private String armarCuerpo(PlantillaNotificacion plantilla, Map<String, String> datos) {
        String cuerpo = switch (plantilla) {
            case GANADOR_SUBASTA -> """
                    <p>Hola {nombre},</p>
                    <p>Su oferta de <b>${valor}</b> por <b>{bien}</b> fue la más alta y ganó la subasta.</p>
                    <p>Para continuar, entre a <a href="{enlace}">{enlace}</a> e inicie la compra.</p>""";
            default -> "<p>{texto}</p>";
        };
        return reemplazar(cuerpo, datos);
    }

    private static String reemplazar(String texto, Map<String, String> datos) {
        String resultado = texto;
        if (datos != null) {
            for (Map.Entry<String, String> d : datos.entrySet()) {
                resultado = resultado.replace("{" + d.getKey() + "}", String.valueOf(d.getValue()));
            }
        }
        return resultado;
    }
}
