package co.edu.javeriana.sso.dominio.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_carrito")
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_carrito")
    private CarritoSubasta carrito;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_subasta")
    private Subasta subasta;

    public Long getIdItem() { return idItem; }
    public void setIdItem(Long idItem) { this.idItem = idItem; }
    public CarritoSubasta getCarrito() { return carrito; }
    public void setCarrito(CarritoSubasta carrito) { this.carrito = carrito; }
    public Subasta getSubasta() { return subasta; }
    public void setSubasta(Subasta subasta) { this.subasta = subasta; }
}
