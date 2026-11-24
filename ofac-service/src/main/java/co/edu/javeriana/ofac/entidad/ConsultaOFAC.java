package co.edu.javeriana.ofac.entidad;

import co.edu.javeriana.ofac.dto.ResultadoOFAC;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulta_ofac")
public class ConsultaOFAC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsulta;
    private String cedula;
    @Enumerated(EnumType.STRING)
    private ResultadoOFAC resultado;
    private LocalDateTime fechaHora;

    public Long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Long idConsulta) { this.idConsulta = idConsulta; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public ResultadoOFAC getResultado() { return resultado; }
    public void setResultado(ResultadoOFAC resultado) { this.resultado = resultado; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
