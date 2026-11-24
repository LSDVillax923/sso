package co.edu.javeriana.sen.srb;

import co.edu.javeriana.sen.dto.DTOEnvioSEN;
import co.edu.javeriana.sen.entidad.Envio;
import co.edu.javeriana.sen.integra.IntegraAckSEN;
import co.edu.javeriana.sen.repo.RepoEnvio;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regla de negocio de SEN: programa el envío dos días hábiles después y confirma a SSO. */
@Service
public class SRBProgramarEnvio {

    private final RepoEnvio repoEnvio;
    private final IntegraAckSEN integraAckSEN;

    public SRBProgramarEnvio(RepoEnvio repoEnvio, IntegraAckSEN integraAckSEN) {
        this.repoEnvio = repoEnvio;
        this.integraAckSEN = integraAckSEN;
    }

    /** Almacena la información que llega y programa el envío a la dirección registrada del cliente. */
    @Transactional
    public void almacenarEnvio(DTOEnvioSEN dto) {
        if (!repoEnvio.existsByNumeroCompra(dto.numeroCompra())) {
            registrarEnvio(dto, programarFecha());
        }
        integraAckSEN.enviarAck(dto.numeroCompra());
    }

    /** Dos días hábiles después de hoy. */
    private LocalDate programarFecha() {
        LocalDate fecha = LocalDate.now();
        int habiles = 0;
        while (habiles < 2) {
            fecha = fecha.plusDays(1);
            if (fecha.getDayOfWeek() != DayOfWeek.SATURDAY && fecha.getDayOfWeek() != DayOfWeek.SUNDAY) {
                habiles++;
            }
        }
        return fecha;
    }

    private void registrarEnvio(DTOEnvioSEN dto, LocalDate fechaProgramada) {
        Envio envio = new Envio();
        envio.setNumeroCompra(dto.numeroCompra());
        envio.setIdBien(dto.idBien());
        envio.setDescripcion(dto.descripcion());
        envio.setCedula(dto.cedula());
        envio.setDestinatario(dto.nombre());
        envio.setDireccion(dto.direccion());
        envio.setCelular(dto.celular());
        envio.setFechaProgramada(fechaProgramada);
        envio.setEstado("PROGRAMADO");
        repoEnvio.save(envio);
    }
}
