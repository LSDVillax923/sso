package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.EstadoSubasta;
import co.edu.javeriana.sso.dominio.entidad.Subasta;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepoSubasta extends JpaRepository<Subasta, Long> {

    @Query("""
            select s from Subasta s join fetch s.bien b
            where s.estado = :estado and s.fechaInicio <= :ahora and s.fechaCierre >= :ahora
              and (:filtro = '' or lower(b.nombre) like lower(concat('%', :filtro, '%'))
                   or lower(b.descripcion) like lower(concat('%', :filtro, '%')))
            order by s.fechaCierre""")
    List<Subasta> buscarPorEstado(@Param("estado") EstadoSubasta estado, @Param("filtro") String filtro,
                                  @Param("ahora") LocalDateTime ahora);

    default List<Subasta> buscarAbiertas(String filtro, LocalDateTime ahora) {
        return buscarPorEstado(EstadoSubasta.ABIERTA, filtro == null ? "" : filtro.trim(), ahora);
    }

    List<Subasta> findByEstadoAndFechaCierreBefore(EstadoSubasta estado, LocalDateTime fecha);

    List<Subasta> findByEstadoAndFechaInicioBefore(EstadoSubasta estado, LocalDateTime fecha);
}
