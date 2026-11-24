package co.edu.javeriana.sso.dominio.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    private String cedula;
    @Column(unique = true, nullable = false)
    private String usuarioAD;
    private String nombre;
    private String email;
    private String celular;
    private String direccion;

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getUsuarioAD() { return usuarioAD; }
    public void setUsuarioAD(String usuarioAD) { this.usuarioAD = usuarioAD; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
