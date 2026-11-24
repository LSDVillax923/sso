package co.edu.javeriana.sso.dominio.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroCompra;
    @OneToOne(optional = false)
    @JoinColumn(name = "id_oferta", unique = true)
    private Oferta oferta;
    @ManyToOne(optional = false)
    @JoinColumn(name = "cedula")
    private Cliente cliente;
    private BigDecimal valorOferta;
    private BigDecimal impuestos;
    private BigDecimal valorTotal;
    @Enumerated(EnumType.STRING)
    private EstadoCompra estado;
    private int intentosOFAC;
    private LocalDateTime proximoIntento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void alCrear() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = fechaCreacion;
    }

    @PreUpdate
    void alActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Long getNumeroCompra() { return numeroCompra; }
    public void setNumeroCompra(Long numeroCompra) { this.numeroCompra = numeroCompra; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public BigDecimal getValorOferta() { return valorOferta; }
    public void setValorOferta(BigDecimal valorOferta) { this.valorOferta = valorOferta; }
    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public EstadoCompra getEstado() { return estado; }
    public void setEstado(EstadoCompra estado) { this.estado = estado; }
    public int getIntentosOFAC() { return intentosOFAC; }
    public void setIntentosOFAC(int intentosOFAC) { this.intentosOFAC = intentosOFAC; }
    public LocalDateTime getProximoIntento() { return proximoIntento; }
    public void setProximoIntento(LocalDateTime proximoIntento) { this.proximoIntento = proximoIntento; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
