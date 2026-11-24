package co.edu.javeriana.ofac.repo;

import co.edu.javeriana.ofac.entidad.PersonaReportada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoPersonaReportada extends JpaRepository<PersonaReportada, String> {

    boolean existsByCedula(String cedula);
}
