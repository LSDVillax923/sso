package co.edu.javeriana.sin.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_inventario")
public class ItemInventario {

    @Id
    private Long idBien;
    private String descripcion;
    private int cantidadDisponible;
    private String estado;

    public Long getIdBien() { return idBien; }
    public void setIdBien(Long idBien) { this.idBien = idBien; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
