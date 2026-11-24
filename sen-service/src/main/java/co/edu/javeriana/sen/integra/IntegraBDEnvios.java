package co.edu.javeriana.sen.integra;

import co.edu.javeriana.sen.dto.DTOEnvioSEN;
import co.edu.javeriana.sen.entidad.Envio;
import co.edu.javeriana.sen.repo.RepoEnvio;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/** Única salida de SEN hacia bd_sen (JDBC 5432): guarda el envío programado. */
@Component
public class IntegraBDEnvios {

    private final RepoEnvio repoEnvio;

    public IntegraBDEnvios(RepoEnvio repoEnvio) {
        this.repoEnvio = repoEnvio;
    }

    public boolean envioRegistrado(Long numeroCompra) {
        return repoEnvio.existsByNumeroCompra(numeroCompra);
    }

    public void registrarEnvio(DTOEnvioSEN dto, LocalDate fechaProgramada) {
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
