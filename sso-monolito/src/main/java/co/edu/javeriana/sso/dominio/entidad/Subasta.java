package co.edu.javeriana.sso.dominio.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "subasta")
public class Subasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubasta;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_bien")
    private Bien bien;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCierre;
    @Enumerated(EnumType.STRING)
    private EstadoSubasta estado;

    /** Solo se oferta entre la fecha de inicio y la de cierre, y con la subasta ABIERTA. */
    public boolean estaAbierta(LocalDateTime ahora) {
        return estado == EstadoSubasta.ABIERTA && !ahora.isBefore(fechaInicio) && !ahora.isAfter(fechaCierre);
    }

    public Long getIdSubasta() { return idSubasta; }
    public void setIdSubasta(Long idSubasta) { this.idSubasta = idSubasta; }
    public Bien getBien() { return bien; }
    public void setBien(Bien bien) { this.bien = bien; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public EstadoSubasta getEstado() { return estado; }
    public void setEstado(EstadoSubasta estado) { this.estado = estado; }
}
