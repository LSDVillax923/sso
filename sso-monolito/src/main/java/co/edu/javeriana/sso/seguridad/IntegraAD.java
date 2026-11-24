package co.edu.javeriana.sso.seguridad;

import co.edu.javeriana.sso.dominio.error.ErrorNegocio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Component;

/** Valida usuario y clave contra el Active Directory donde se autentican todas las aplicaciones de la empresa. */
@Component
public class IntegraAD {

    private static final Logger log = LoggerFactory.getLogger(IntegraAD.class);

    private final LdapTemplate ldapTemplate;
    private final boolean habilitado;
    private final String baseUsuarios;

    public IntegraAD(LdapTemplate ldapTemplate, @Value("${sso.ad.habilitado:true}") boolean habilitado,
                     @Value("${sso.ad.base-usuarios:ou=clientes}") String baseUsuarios) {
        this.ldapTemplate = ldapTemplate;
        this.habilitado = habilitado;
        this.baseUsuarios = baseUsuarios;
    }

    public boolean validarCredenciales(String usuario, String clave) {
        if (!habilitado) {
            log.warn("AD deshabilitado (sso.ad.habilitado=false): se acepta a {} sin validar la clave", usuario);
            return true;
        }
        try {
            return ldapTemplate.authenticate(baseUsuarios, new EqualsFilter("uid", usuario).encode(), clave);
        } catch (CommunicationException e) {
            throw new ErrorNegocio(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de autenticación no está disponible, intente más tarde");
        }
    }
}
