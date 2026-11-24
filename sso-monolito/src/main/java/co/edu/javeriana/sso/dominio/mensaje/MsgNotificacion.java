package co.edu.javeriana.sso.dominio.mensaje;

import java.util.Map;

public record MsgNotificacion(String canal, String destino, PlantillaMensaje plantilla, Map<String, String> datos) {

    public static MsgNotificacion correo(String email, PlantillaMensaje plantilla, Map<String, String> datos) {
        return new MsgNotificacion("CORREO", email, plantilla, datos);
    }

    public static MsgNotificacion sms(String celular, PlantillaMensaje plantilla, Map<String, String> datos) {
        return new MsgNotificacion("SMS", celular, plantilla, datos);
    }
}
