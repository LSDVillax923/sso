package co.edu.javeriana.ad;

/** Entrada objectClass inetOrgPerson del directorio. memberOf se calcula con los grupos que lo nombran. */
public record UsuarioAD(String uid, String cn, String mail, String userPassword, String memberOf) {
}
