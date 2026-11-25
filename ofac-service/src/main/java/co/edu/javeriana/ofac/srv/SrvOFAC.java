package co.edu.javeriana.ofac.srv;

import co.edu.javeriana.ofac.dto.RespuestaOFAC;
import co.edu.javeriana.ofac.dto.ResultadoOFAC;
import co.edu.javeriana.ofac.repo.RepoPersonaReportada;
import co.edu.javeriana.ofac.repo.RepoSolicitudOFAC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvOFAC {

    private final RepoPersonaReportada repoPersonaReportada;
    private final RepoSolicitudOFAC repoSolicitudOFAC;

    public SrvOFAC(RepoPersonaReportada repoPersonaReportada, RepoSolicitudOFAC repoSolicitudOFAC) {
        this.repoPersonaReportada = repoPersonaReportada;
        this.repoSolicitudOFAC = repoSolicitudOFAC;
    }

    @Transactional(readOnly = true)
    public RespuestaOFAC validar(String cedula) {
        ResultadoOFAC resultado;
        if (repoPersonaReportada.existsByCedula(cedula)) {
            resultado = ResultadoOFAC.RECHAZADO;
        } else if (repoSolicitudOFAC.existsByCedulaAndEstado(cedula, "EN_REVISION")) {
            resultado = ResultadoOFAC.ENPROCESO;
        } else {
            resultado = ResultadoOFAC.APROBADO;
        }
        return new RespuestaOFAC(cedula, resultado);
    }
}
