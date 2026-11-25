package co.edu.javeriana.sso.seguridad;

import co.edu.javeriana.sso.dominio.error.ErrorNegocio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Component;

/** Valida usuario y clave contra el Active Directory donde se autentican todas las aplicaciones de la empresa. */
@Component
public class IntegraAD {

    private final LdapTemplate ldapTemplate;
    private final String baseUsuarios;

    public IntegraAD(LdapTemplate ldapTemplate,
                     @Value("${sso.ad.base-usuarios:ou=clientes}") String baseUsuarios) {
        this.ldapTemplate = ldapTemplate;
        this.baseUsuarios = baseUsuarios;
    }

    public boolean validarCredenciales(String usuario, String clave) {
        try {
            return ldapTemplate.authenticate(baseUsuarios, new EqualsFilter("uid", usuario).encode(), clave);
        } catch (CommunicationException e) {
            throw new ErrorNegocio("El servicio de autenticación no está disponible, intente más tarde");
        }
    }
}
