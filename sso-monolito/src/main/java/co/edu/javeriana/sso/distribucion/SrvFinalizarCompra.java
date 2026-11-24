package co.edu.javeriana.sso.distribucion;

import co.edu.javeriana.sso.dominio.entidad.AckCompra;
import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.entidad.SistemaDestino;
import co.edu.javeriana.sso.dominio.repo.RepoAckCompra;
import co.edu.javeriana.sso.dominio.repo.RepoCompra;
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

    private final RepoAckCompra repoAckCompra;
    private final RepoCompra repoCompra;
    private final SrvDistribuirCompra srvDistribuirCompra;
    private final PublicadorEventos publicadorEventos;
    private final int esperaMin;

    public SrvFinalizarCompra(RepoAckCompra repoAckCompra, RepoCompra repoCompra, SrvDistribuirCompra srvDistribuirCompra,
                              PublicadorEventos publicadorEventos, @Value("${sso.ack.espera-min:10}") int esperaMin) {
        this.repoAckCompra = repoAckCompra;
        this.repoCompra = repoCompra;
        this.srvDistribuirCompra = srvDistribuirCompra;
        this.publicadorEventos = publicadorEventos;
        this.esperaMin = esperaMin;
    }

    @Transactional
    public void registrarAck(MsgAck ack) {
        Compra compra = repoCompra.bloquear(ack.numeroCompra()).orElse(null);
        if (compra == null || ack.sistema() == null) {
            return;
        }
        if (!repoAckCompra.existsByCompraAndSistema(compra, ack.sistema())) {
            AckCompra nuevo = new AckCompra();
            nuevo.setCompra(compra);
            nuevo.setSistema(ack.sistema());
            nuevo.setFechaRecibido(LocalDateTime.now());
            repoAckCompra.save(nuevo);
        }
        finalizarSiCompleta(compra);
    }

    @Transactional
    public void revisarAckPendientes() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(esperaMin);
        for (Compra compra : repoCompra.findByEstadoAndFechaActualizacionBefore(EstadoCompra.DISTRIBUIDA, limite)) {
            if (repoAckCompra.countByCompra(compra) >= SistemaDestino.values().length) {
                finalizarSiCompleta(compra);
                continue;
            }
            for (SistemaDestino sistema : SistemaDestino.values()) {
                if (!repoAckCompra.existsByCompraAndSistema(compra, sistema)) {
                    srvDistribuirCompra.reenviar(compra, sistema);
                }
            }
            compra.setFechaActualizacion(LocalDateTime.now());
            repoCompra.save(compra);
        }
    }

    private void finalizarSiCompleta(Compra compra) {
        if (compra.getEstado() != EstadoCompra.DISTRIBUIDA
                || repoAckCompra.countByCompra(compra) < SistemaDestino.values().length) {
            return;
        }
        compra.setEstado(EstadoCompra.TERMINADA);
        repoCompra.save(compra);
        publicadorEventos.publicarNotificacion(MsgNotificacion.sms(compra.getCliente().getCelular(),
                PlantillaMensaje.COMPRA_TERMINADA, Map.of("numeroCompra", String.valueOf(compra.getNumeroCompra()))));
    }
}
