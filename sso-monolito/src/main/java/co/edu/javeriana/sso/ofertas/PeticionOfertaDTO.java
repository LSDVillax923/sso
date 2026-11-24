package co.edu.javeriana.sso.ofertas;

import java.util.List;

/** Petición de subasta de uno o varios bienes del carrito. */
public record PeticionOfertaDTO(List<ItemOfertaDTO> items) {
}
