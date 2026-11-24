package co.edu.javeriana.ofac.srv;

import co.edu.javeriana.ofac.dto.RespuestaOFAC;
import co.edu.javeriana.ofac.dto.ResultadoOFAC;
import co.edu.javeriana.ofac.entidad.ConsultaOFAC;
import co.edu.javeriana.ofac.repo.RepoConsultaOFAC;
import co.edu.javeriana.ofac.repo.RepoPersonaReportada;
import co.edu.javeriana.ofac.repo.RepoSolicitudOFAC;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvOFAC {

    private final RepoPersonaReportada repoPersonaReportada;
    private final RepoSolicitudOFAC repoSolicitudOFAC;
    private final RepoConsultaOFAC repoConsultaOFAC;

    public SrvOFAC(RepoPersonaReportada repoPersonaReportada, RepoSolicitudOFAC repoSolicitudOFAC,
                   RepoConsultaOFAC repoConsultaOFAC) {
        this.repoPersonaReportada = repoPersonaReportada;
        this.repoSolicitudOFAC = repoSolicitudOFAC;
        this.repoConsultaOFAC = repoConsultaOFAC;
    }

    @Transactional
    public RespuestaOFAC validar(String cedula) {
        ResultadoOFAC resultado;
        if (repoPersonaReportada.existsByCedula(cedula)) {
            resultado = ResultadoOFAC.RECHAZADO;
        } else if (repoSolicitudOFAC.existsByCedulaAndEstado(cedula, "EN_REVISION")) {
            resultado = ResultadoOFAC.ENPROCESO;
        } else {
            resultado = ResultadoOFAC.APROBADO;
        }
        ConsultaOFAC consulta = new ConsultaOFAC();
        consulta.setCedula(cedula);
        consulta.setResultado(resultado);
        consulta.setFechaHora(LocalDateTime.now());
        repoConsultaOFAC.save(consulta);
        return new RespuestaOFAC(cedula, resultado, consulta.getFechaHora());
    }
}
