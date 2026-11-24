package co.edu.javeriana.correo;

import org.springframework.stereotype.Service;

/** Agente de entrega: deja cada correo recibido en el buzón de su destinatario. */
@Service
public class SRBEntregarCorreo {

    private final IntegraBuzon integraBuzon;

    public SRBEntregarCorreo(IntegraBuzon integraBuzon) {
        this.integraBuzon = integraBuzon;
    }

    public void entregar(String para, String asunto, String cuerpo) {
        if (para == null || !para.contains("@")) {
            throw new IllegalArgumentException("Destinatario no válido: " + para);
        }
        integraBuzon.guardar(para.trim().toLowerCase(), asunto == null || asunto.isBlank() ? "(sin asunto)" : asunto,
                cuerpo == null ? "" : cuerpo);
    }
}
