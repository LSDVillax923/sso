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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** La compra queda TERMINADA solo cuando SIN y SEN confirman que recibieron la información. */
@Service
public class SrvFinalizarCompra {

    private final RepoAckCompra repoAckCompra;
    private final RepoCompra repoCompra;
    private final PublicadorEventos publicadorEventos;

    public SrvFinalizarCompra(RepoAckCompra repoAckCompra, RepoCompra repoCompra,
                              PublicadorEventos publicadorEventos) {
        this.repoAckCompra = repoAckCompra;
        this.repoCompra = repoCompra;
        this.publicadorEventos = publicadorEventos;
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
