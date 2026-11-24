package co.edu.javeriana.notificar.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** Cada correo o SMS que pidió el SSO, con su resultado. */
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    private String canal;
    private String destino;
    private String plantilla;

    @Column(length = 4000)
    private String texto;

    @Enumerated(EnumType.STRING)
    private EstadoNotificacion estado;

    private int intentos;
    private LocalDateTime fecha;

    protected Notificacion() {
    }

    public Notificacion(String canal, String destino, String plantilla, String texto) {
        this.canal = canal;
        this.destino = destino;
        this.plantilla = plantilla;
        this.texto = texto;
        this.estado = EstadoNotificacion.PENDIENTE;
        this.fecha = LocalDateTime.now();
    }

    public void marcar(EstadoNotificacion nuevo) {
        this.estado = nuevo;
        this.intentos++;
    }

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public String getCanal() {
        return canal;
    }

    public String getDestino() {
        return destino;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public String getTexto() {
        return texto;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public int getIntentos() {
        return intentos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
