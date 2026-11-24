package co.edu.javeriana.sin.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta_recibida")
public class VentaRecibida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;
    @Column(unique = true)
    private Long numeroCompra;
    private Long idBien;
    private String descripcion;
    private int cantidad;
    private LocalDateTime fechaRecepcion;

    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }
    public Long getNumeroCompra() { return numeroCompra; }
    public void setNumeroCompra(Long numeroCompra) { this.numeroCompra = numeroCompra; }
    public Long getIdBien() { return idBien; }
    public void setIdBien(Long idBien) { this.idBien = idBien; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }
}
