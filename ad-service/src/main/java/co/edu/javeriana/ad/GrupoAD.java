package co.edu.javeriana.ad;

import java.util.List;

/** Entrada objectClass groupOfNames, por ejemplo sso-clientes. */
public record GrupoAD(String cn, List<String> member) {
}
