package co.edu.javeriana.ofac.srv;

import co.edu.javeriana.ofac.dto.RespuestaOFAC;
import co.edu.javeriana.ofac.dto.ResultadoOFAC;
import co.edu.javeriana.ofac.entidad.ConsultaOFAC;
import co.edu.javeriana.ofac.integra.IntegraBDOFAC;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvOFAC {

    private final IntegraBDOFAC integraBDOFAC;

    public SrvOFAC(IntegraBDOFAC integraBDOFAC) {
        this.integraBDOFAC = integraBDOFAC;
    }

    @Transactional
    public RespuestaOFAC validar(String cedula) {
        ResultadoOFAC resultado;
        if (integraBDOFAC.estaReportada(cedula)) {
            resultado = ResultadoOFAC.RECHAZADO;
        } else if (integraBDOFAC.tieneSolicitudEnRevision(cedula)) {
            resultado = ResultadoOFAC.ENPROCESO;
        } else {
            resultado = ResultadoOFAC.APROBADO;
        }
        ConsultaOFAC consulta = new ConsultaOFAC();
        consulta.setCedula(cedula);
        consulta.setResultado(resultado);
        consulta.setFechaHora(LocalDateTime.now());
        integraBDOFAC.registrarConsulta(consulta);
        return new RespuestaOFAC(cedula, resultado, consulta.getFechaHora());
    }
}
