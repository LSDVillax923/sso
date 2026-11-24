package co.edu.javeriana.sso.subastas;

import co.edu.javeriana.sso.dominio.entidad.Cliente;
import co.edu.javeriana.sso.dominio.entidad.EstadoOferta;
import co.edu.javeriana.sso.dominio.entidad.EstadoSubasta;
import co.edu.javeriana.sso.dominio.entidad.Oferta;
import co.edu.javeriana.sso.dominio.entidad.Subasta;
import co.edu.javeriana.sso.dominio.repo.RepoOferta;
import co.edu.javeriana.sso.dominio.repo.RepoSubasta;
import co.edu.javeriana.sso.dominio.mensaje.MsgNotificacion;
import co.edu.javeriana.sso.dominio.mensaje.PlantillaMensaje;
import co.edu.javeriana.sso.notificacion.PublicadorEventos;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvCierreSubasta {

    private final RepoSubasta repoSubasta;
    private final RepoOferta repoOferta;
    private final PublicadorEventos publicadorEventos;
    private final String urlFront;

    public SrvCierreSubasta(RepoSubasta repoSubasta, RepoOferta repoOferta, PublicadorEventos publicadorEventos,
                            @Value("${sso.url-front}") String urlFront) {
        this.repoSubasta = repoSubasta;
        this.repoOferta = repoOferta;
        this.publicadorEventos = publicadorEventos;
        this.urlFront = urlFront;
    }

    @Transactional
    public void abrirSubastasProgramadas() {
        repoSubasta.findByEstadoAndFechaInicioBefore(EstadoSubasta.PROGRAMADA, LocalDateTime.now())
                .forEach(s -> s.setEstado(EstadoSubasta.ABIERTA));
    }

    @Transactional
    public void cerrarSubastasVencidas() {
        for (Subasta subasta : repoSubasta.findByEstadoAndFechaCierreBefore(EstadoSubasta.ABIERTA, LocalDateTime.now())) {
            Optional<Oferta> ganadora = determinarGanador(subasta);
            if (ganadora.isEmpty()) {
                subasta.setEstado(EstadoSubasta.DESIERTA);
                continue;
            }
            Long idGanadora = ganadora.get().getIdOferta();
            repoOferta.findBySubasta(subasta).forEach(o ->
                    o.setEstado(o.getIdOferta().equals(idGanadora) ? EstadoOferta.GANADORA : EstadoOferta.PERDEDORA));
            subasta.setEstado(EstadoSubasta.ADJUDICADA);
            notificarGanador(ganadora.get());
        }
    }

    private Optional<Oferta> determinarGanador(Subasta subasta) {
        return repoOferta.findTopBySubastaOrderByValorDescFechaHoraAsc(subasta);
    }

    private void notificarGanador(Oferta oferta) {
        Cliente cliente = oferta.getCliente();
        Map<String, String> datos = Map.of(
                "nombre", String.valueOf(cliente.getNombre()),
                "bien", String.valueOf(oferta.getSubasta().getBien().getNombre()),
                "valor", oferta.getValor().toPlainString(),
                "enlace", urlFront + "/mis-subastas");
        publicadorEventos.publicarNotificacion(MsgNotificacion.correo(cliente.getEmail(), PlantillaMensaje.GANADOR_SUBASTA, datos));
    }
}
