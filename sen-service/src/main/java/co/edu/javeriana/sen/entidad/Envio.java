package co.edu.javeriana.sen.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "envio")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEnvio;
    @Column(unique = true)
    private Long numeroCompra;
    private Long idBien;
    private String descripcion;
    private String cedula;
    private String destinatario;
    private String direccion;
    private String celular;
    private LocalDate fechaProgramada;
    private String estado;

    public Long getIdEnvio() { return idEnvio; }
    public void setIdEnvio(Long idEnvio) { this.idEnvio = idEnvio; }
    public Long getNumeroCompra() { return numeroCompra; }
    public void setNumeroCompra(Long numeroCompra) { this.numeroCompra = numeroCompra; }
    public Long getIdBien() { return idBien; }
    public void setIdBien(Long idBien) { this.idBien = idBien; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
