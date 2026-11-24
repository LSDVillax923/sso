package co.edu.javeriana.ofac.repo;

import co.edu.javeriana.ofac.entidad.ConsultaOFAC;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoConsultaOFAC extends JpaRepository<ConsultaOFAC, Long> {
}
