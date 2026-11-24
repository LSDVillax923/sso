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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name = "ack_compra", uniqueConstraints = @UniqueConstraint(columnNames = {"numero_compra", "sistema"}))
public class AckCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAck;
    @ManyToOne(optional = false)
    @JoinColumn(name = "numero_compra")
    private Compra compra;
    @Enumerated(EnumType.STRING)
    private SistemaDestino sistema;
    private LocalDateTime fechaRecibido;

    public Long getIdAck() { return idAck; }
    public void setIdAck(Long idAck) { this.idAck = idAck; }
    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }
    public SistemaDestino getSistema() { return sistema; }
    public void setSistema(SistemaDestino sistema) { this.sistema = sistema; }
    public LocalDateTime getFechaRecibido() { return fechaRecibido; }
    public void setFechaRecibido(LocalDateTime fechaRecibido) { this.fechaRecibido = fechaRecibido; }
}
