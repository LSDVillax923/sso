package co.edu.javeriana.notificar.srb;

import co.edu.javeriana.notificar.dto.DTONotificacion;
import co.edu.javeriana.notificar.dto.PlantillaNotificacion;
import co.edu.javeriana.notificar.integra.IntegraGatewaySMS;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Arma el SMS de la plantilla y lo manda por IntegraGatewaySMS. */
@Service
public class SRBEnviarSMS {

    private final IntegraGatewaySMS integraGatewaySMS;

    public SRBEnviarSMS(IntegraGatewaySMS integraGatewaySMS) {
        this.integraGatewaySMS = integraGatewaySMS;
    }

    public void enviar(DTONotificacion dto) {
        String texto = armarTexto(dto.plantilla(), dto.datos());
        if (!integraGatewaySMS.enviarSMS(dto.destino(), texto)) {
            throw new IllegalStateException("El gateway SMS no respondió");
        }
    }

    private String armarTexto(PlantillaNotificacion plantilla, Map<String, String> datos) {
        String texto = switch (plantilla) {
            case GANADOR_SUBASTA -> "SSO: ganó la subasta de {bien}. Revise su correo para iniciar la compra.";
            case OFAC_APROBADO -> "SSO: su compra {numeroCompra} fue aprobada. Valor a pagar con impuestos: ${valor}. Pague en {url}";
            case OFAC_RECHAZADO -> "SSO: no es posible continuar la compra {numeroCompra}. Su identificación no está habilitada para compras.";
            case OFAC_EN_VERIFICACION -> "SSO: su compra {numeroCompra} sigue en verificación. Le avisaremos por este medio.";
            case COMPRA_TERMINADA -> "SSO: su compra {numeroCompra} fue realizada. El envío quedó programado a su dirección registrada.";
        };
        if (datos != null) {
            for (Map.Entry<String, String> d : datos.entrySet()) {
                texto = texto.replace("{" + d.getKey() + "}", String.valueOf(d.getValue()));
            }
        }
        return texto;
    }
}
