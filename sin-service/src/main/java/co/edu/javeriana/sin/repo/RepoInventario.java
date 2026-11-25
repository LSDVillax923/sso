package co.edu.javeriana.sin.repo;

import co.edu.javeriana.sin.entidad.ItemInventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoInventario extends JpaRepository<ItemInventario, Long> {
}
