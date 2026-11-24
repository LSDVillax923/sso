package co.edu.javeriana.notificar.srb;

import co.edu.javeriana.notificar.dto.DTONotificacion;
import co.edu.javeriana.notificar.dto.PlantillaNotificacion;
import co.edu.javeriana.notificar.entidad.EstadoNotificacion;
import co.edu.javeriana.notificar.integra.IntegraBDNotificaciones;
import co.edu.javeriana.notificar.integra.IntegraSMTP;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Arma el correo de la plantilla, lo registra en bd_notificaciones por IntegraBDNotificaciones y lo manda por IntegraSMTP. */
@Service
public class SRBEnviarCorreo {

    private final IntegraSMTP integraSMTP;
    private final IntegraBDNotificaciones integraBDNotificaciones;

    public SRBEnviarCorreo(IntegraSMTP integraSMTP, IntegraBDNotificaciones integraBDNotificaciones) {
        this.integraSMTP = integraSMTP;
        this.integraBDNotificaciones = integraBDNotificaciones;
    }

    public void enviar(DTONotificacion dto) {
        String asunto = dto.plantilla() == PlantillaNotificacion.GANADOR_SUBASTA
                ? reemplazar("SSO: ganó la subasta de {bien}", dto.datos())
                : "SSO: novedades de su compra";
        String cuerpo = armarCuerpo(dto.plantilla(), dto.datos());
        Long idNotificacion = integraBDNotificaciones.registrar("CORREO", dto.destino(), dto.plantilla().name(), cuerpo);
        try {
            integraSMTP.enviarCorreo(dto.destino(), asunto, cuerpo);
            integraBDNotificaciones.marcar(idNotificacion, EstadoNotificacion.ENVIADA);
        } catch (RuntimeException e) {
            // RabbitMQ reintenta; al tercer fallo el aviso queda en notif.correo.dlq
            integraBDNotificaciones.marcar(idNotificacion, EstadoNotificacion.FALLIDA);
            throw e;
        }
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
