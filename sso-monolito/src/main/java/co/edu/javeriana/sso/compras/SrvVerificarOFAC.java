package co.edu.javeriana.sso.compras;

import co.edu.javeriana.sso.pagos.SrvRegistroYoPago;

import co.edu.javeriana.sso.compras.EstadoOFAC;
import co.edu.javeriana.sso.compras.RespuestaOFACDTO;
import co.edu.javeriana.sso.notificacion.PublicadorEventos;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.mensaje.MsgNotificacion;
import co.edu.javeriana.sso.dominio.mensaje.PlantillaMensaje;
import co.edu.javeriana.sso.dominio.integra.IntegraBDCompra;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Verifica al cliente en la OFAC. APROBADO sigue a YoPago, RECHAZADO termina, ENPROCESO o caída de red se reintenta. */
@Service
public class SrvVerificarOFAC {

    private final IntegraBDCompra integraBDCompra;
    private final SrvRegistroYoPago srvRegistroYoPago;
    private final PublicadorEventos publicadorEventos;
    private final int maxIntentos;
    private final int esperaMin;

    public SrvVerificarOFAC(IntegraBDCompra integraBDCompra, SrvRegistroYoPago srvRegistroYoPago,
                            PublicadorEventos publicadorEventos, @Value("${sso.ofac.max-intentos:6}") int maxIntentos,
                            @Value("${sso.ofac.espera-min:5}") int esperaMin) {
        this.integraBDCompra = integraBDCompra;
        this.srvRegistroYoPago = srvRegistroYoPago;
        this.publicadorEventos = publicadorEventos;
        this.maxIntentos = maxIntentos;
        this.esperaMin = esperaMin;
    }

    @Transactional
    public void verificar(Long numeroCompra, RespuestaOFACDTO respuesta) {
        Compra compra = integraBDCompra.findByNumeroCompra(numeroCompra).orElse(null);
        if (compra == null || compra.getEstado() != EstadoCompra.VERIFICANDO_OFAC) {
            return; // mensaje repetido: la compra ya salió de verificación
        }
        procesarRespuesta(compra, respuesta);
    }

    private void procesarRespuesta(Compra compra, RespuestaOFACDTO respuesta) {
        EstadoOFAC estado = respuesta == null || respuesta.estado() == null ? EstadoOFAC.ENPROCESO : respuesta.estado();
        switch (estado) {
            case APROBADO -> {
                compra.setEstado(EstadoCompra.APROBADA_OFAC);
                integraBDCompra.save(compra);
                srvRegistroYoPago.registrarCompraPendiente(compra);
            }
            case RECHAZADO -> {
                compra.setEstado(EstadoCompra.RECHAZADA_OFAC);
                integraBDCompra.save(compra);
                publicadorEventos.publicarNotificacion(MsgNotificacion.sms(compra.getCliente().getCelular(),
                        PlantillaMensaje.OFAC_RECHAZADO, Map.of("numeroCompra", String.valueOf(compra.getNumeroCompra()))));
            }
            case ENPROCESO -> programarReintento(compra);
        }
    }

    private void programarReintento(Compra compra) {
        compra.setIntentosOFAC(compra.getIntentosOFAC() + 1);
        compra.setProximoIntento(LocalDateTime.now().plusMinutes(esperaMin));
        integraBDCompra.save(compra);
        if (compra.getIntentosOFAC() == maxIntentos) {
            publicadorEventos.publicarNotificacion(MsgNotificacion.sms(compra.getCliente().getCelular(),
                    PlantillaMensaje.OFAC_EN_VERIFICACION, Map.of("numeroCompra", String.valueOf(compra.getNumeroCompra()))));
        }
    }
}
