package co.edu.javeriana.sso.distribucion;

import co.edu.javeriana.sso.dominio.entidad.AckCompra;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.entidad.SistemaDestino;
import co.edu.javeriana.sso.dominio.integra.IntegraBDAckCompra;
import co.edu.javeriana.sso.dominio.integra.IntegraBDCompra;
import co.edu.javeriana.sso.dominio.mensaje.MsgAck;
import co.edu.javeriana.sso.dominio.mensaje.MsgNotificacion;
import co.edu.javeriana.sso.dominio.mensaje.PlantillaMensaje;
import co.edu.javeriana.sso.notificacion.PublicadorEventos;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** La compra queda TERMINADA solo cuando SIN y SEN confirman que recibieron la información. */
@Service
public class SrvFinalizarCompra {

    private final IntegraBDAckCompra integraBDAckCompra;
    private final IntegraBDCompra integraBDCompra;
    private final SrvDistribuirCompra srvDistribuirCompra;
    private final PublicadorEventos publicadorEventos;
    private final int esperaMin;

    public SrvFinalizarCompra(IntegraBDAckCompra integraBDAckCompra, IntegraBDCompra integraBDCompra, SrvDistribuirCompra srvDistribuirCompra,
                              PublicadorEventos publicadorEventos, @Value("${sso.ack.espera-min:10}") int esperaMin) {
        this.integraBDAckCompra = integraBDAckCompra;
        this.integraBDCompra = integraBDCompra;
        this.srvDistribuirCompra = srvDistribuirCompra;
        this.publicadorEventos = publicadorEventos;
        this.esperaMin = esperaMin;
    }

    @Transactional
    public void registrarAck(MsgAck ack) {
        Compra compra = integraBDCompra.bloquear(ack.numeroCompra()).orElse(null);
        if (compra == null || ack.sistema() == null) {
            return;
        }
        if (!integraBDAckCompra.existsByCompraAndSistema(compra, ack.sistema())) {
            AckCompra nuevo = new AckCompra();
            nuevo.setCompra(compra);
            nuevo.setSistema(ack.sistema());
            nuevo.setFechaRecibido(LocalDateTime.now());
            integraBDAckCompra.save(nuevo);
        }
        finalizarSiCompleta(compra);
    }

    @Transactional
    public void revisarAckPendientes() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(esperaMin);
        for (Compra compra : integraBDCompra.findByEstadoAndFechaActualizacionBefore(EstadoCompra.DISTRIBUIDA, limite)) {
            if (integraBDAckCompra.countByCompra(compra) >= SistemaDestino.values().length) {
                finalizarSiCompleta(compra);
                continue;
            }
            for (SistemaDestino sistema : SistemaDestino.values()) {
                if (!integraBDAckCompra.existsByCompraAndSistema(compra, sistema)) {
                    srvDistribuirCompra.reenviar(compra, sistema);
                }
            }
            compra.setFechaActualizacion(LocalDateTime.now());
            integraBDCompra.save(compra);
        }
    }

    private void finalizarSiCompleta(Compra compra) {
        if (compra.getEstado() != EstadoCompra.DISTRIBUIDA
                || integraBDAckCompra.countByCompra(compra) < SistemaDestino.values().length) {
            return;
        }
        compra.setEstado(EstadoCompra.TERMINADA);
        integraBDCompra.save(compra);
        publicadorEventos.publicarNotificacion(MsgNotificacion.sms(compra.getCliente().getCelular(),
                PlantillaMensaje.COMPRA_TERMINADA, Map.of("numeroCompra", String.valueOf(compra.getNumeroCompra()))));
    }
}
