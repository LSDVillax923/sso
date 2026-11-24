package co.edu.javeriana.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Despacho del proveedor: intenta entregar el SMS al operador móvil hasta 3 veces y deja el resultado en bd_sms. */
@Service
public class SRBDespacharSMS {

    private static final Logger log = LoggerFactory.getLogger(SRBDespacharSMS.class);

    private final IntegraOperadorMovil integraOperadorMovil;
    private final int reintentos;

    public SRBDespacharSMS(IntegraOperadorMovil integraOperadorMovil, @Value("${gateway.reintentos:3}") int reintentos) {
        this.integraOperadorMovil = integraOperadorMovil;
        this.reintentos = reintentos;
    }

    public void despachar(String numero, String texto) {
        for (int intento = 1; intento <= reintentos; intento++) {
            try {
                integraOperadorMovil.entregar(numero, texto);
                integraOperadorMovil.registrar(numero, texto, "ENTREGADO");
                return;
            } catch (RuntimeException e) {
                log.warn("Intento {} de {} para {} falló: {}", intento, reintentos, numero, e.getMessage());
            }
        }
        integraOperadorMovil.registrar(numero, texto, "FALLIDO");
    }
}
