package co.edu.javeriana.ofac.integra;

import co.edu.javeriana.ofac.entidad.ConsultaOFAC;
import co.edu.javeriana.ofac.repo.RepoConsultaOFAC;
import co.edu.javeriana.ofac.repo.RepoPersonaReportada;
import co.edu.javeriana.ofac.repo.RepoSolicitudOFAC;
import org.springframework.stereotype.Component;

/** Única salida de OFAC hacia bd_ofac (JDBC 5433): lista de reportados, solicitudes en revisión y registro de consultas. */
@Component
public class IntegraBDOFAC {

    private final RepoPersonaReportada repoPersonaReportada;
    private final RepoSolicitudOFAC repoSolicitudOFAC;
    private final RepoConsultaOFAC repoConsultaOFAC;

    public IntegraBDOFAC(RepoPersonaReportada repoPersonaReportada, RepoSolicitudOFAC repoSolicitudOFAC,
                         RepoConsultaOFAC repoConsultaOFAC) {
        this.repoPersonaReportada = repoPersonaReportada;
        this.repoSolicitudOFAC = repoSolicitudOFAC;
        this.repoConsultaOFAC = repoConsultaOFAC;
    }

    public boolean estaReportada(String cedula) {
        return repoPersonaReportada.existsByCedula(cedula);
    }

    public boolean tieneSolicitudEnRevision(String cedula) {
        return repoSolicitudOFAC.existsByCedulaAndEstado(cedula, "EN_REVISION");
    }

    public ConsultaOFAC registrarConsulta(ConsultaOFAC consulta) {
        return repoConsultaOFAC.save(consulta);
    }
}
