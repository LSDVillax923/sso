package co.edu.javeriana.notificar.srb;

import co.edu.javeriana.notificar.dto.DTONotificacion;
import co.edu.javeriana.notificar.dto.PlantillaNotificacion;
import co.edu.javeriana.notificar.entidad.EstadoNotificacion;
import co.edu.javeriana.notificar.integra.IntegraBDNotificaciones;
import co.edu.javeriana.notificar.integra.IntegraGatewaySMS;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Arma el SMS de la plantilla, lo registra en bd_notificaciones por IntegraBDNotificaciones y lo manda por IntegraGatewaySMS. */
@Service
public class SRBEnviarSMS {

    private final IntegraGatewaySMS integraGatewaySMS;
    private final IntegraBDNotificaciones integraBDNotificaciones;

    public SRBEnviarSMS(IntegraGatewaySMS integraGatewaySMS, IntegraBDNotificaciones integraBDNotificaciones) {
        this.integraGatewaySMS = integraGatewaySMS;
        this.integraBDNotificaciones = integraBDNotificaciones;
    }

    public void enviar(DTONotificacion dto) {
        String texto = armarTexto(dto.plantilla(), dto.datos());
        Long idNotificacion = integraBDNotificaciones.registrar("SMS", dto.destino(), dto.plantilla().name(), texto);
        boolean enviado = integraGatewaySMS.enviarSMS(dto.destino(), texto);
        integraBDNotificaciones.marcar(idNotificacion, enviado ? EstadoNotificacion.ENVIADA : EstadoNotificacion.FALLIDA);
        if (!enviado) {
            // La excepción hace que RabbitMQ reintente; al tercer fallo el aviso queda en notif.sms.dlq
            throw new IllegalStateException("El gateway SMS no respondió");
        }
    }

    private String armarTexto(PlantillaNotificacion plantilla, Map<String, String> datos) {
        String texto = switch (plantilla) {
            case GANADOR_SUBASTA -> "SSO: ganó la subasta de {bien}. Revise su correo para iniciar la compra.";
            case OFAC_APROBADO -> "SSO: su compra {numeroCompra} fue aprobada. Valor a pagar con impuestos: ${valor}. Pague en {url}";
            case OFAC_RECHAZADO -> "SSO: no es posible continuar la compra {numeroCompra}. Su identificación no está habilitada para compras.";
            case OFAC_EN_VERIFICACION -> "SSO: su compra {numeroCompra} sigue en verificación. Le avisaremos por este medio.";
            case PAGO_INCOMPLETO -> "SSO: recibimos ${valorPagado} para la compra {numeroCompra}, pero el total es ${valorTotal}.";
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
