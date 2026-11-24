package co.edu.javeriana.notificar.dto;

import java.util.Map;

/** Lo que publica SSO (MsgNotificacion) en las colas notif.correo y notif.sms. */
public record DTONotificacion(String canal, String destino, PlantillaNotificacion plantilla, Map<String, String> datos) {
}
