package co.edu.javeriana.sso.dominio.repo;

import co.edu.javeriana.sso.dominio.entidad.Compra;
import co.edu.javeriana.sso.dominio.entidad.EstadoCompra;
import co.edu.javeriana.sso.dominio.entidad.Oferta;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepoCompra extends JpaRepository<Compra, Long> {

    Optional<Compra> findByNumeroCompra(Long numeroCompra);

    /** Bloquea la fila: los dos ack pueden llegar a la vez, cada uno a una réplica distinta. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Compra c where c.numeroCompra = :numero")
    Optional<Compra> bloquear(@Param("numero") Long numeroCompra);

    boolean existsByOferta(Oferta oferta);

    List<Compra> findByEstadoAndProximoIntentoBefore(EstadoCompra estado, LocalDateTime fecha);

    List<Compra> findByEstadoAndFechaActualizacionBefore(EstadoCompra estado, LocalDateTime fecha);
}
