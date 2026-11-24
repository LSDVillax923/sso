package co.edu.javeriana.ofac.repo;

import co.edu.javeriana.ofac.entidad.SolicitudOFAC;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoSolicitudOFAC extends JpaRepository<SolicitudOFAC, Long> {

    boolean existsByCedulaAndEstado(String cedula, String estado);
}
