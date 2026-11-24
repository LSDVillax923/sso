package co.edu.javeriana.ad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"ad.puerto-ldap=13389", "ad.puerto=13636", "ad.keystore=target/ad-prueba.p12"})
class ListenerLDAPSTest {

    /** Lo mismo que hace IntegraAD: bind como admin, busca (uid=...) en ou=clientes y hace bind con la clave del usuario. */
    private LdapTemplate comoIntegraAD() throws Exception {
        LdapContextSource fuente = new LdapContextSource();
        fuente.setUrl("ldap://localhost:13389");
        fuente.setBase("dc=sso,dc=local");
        fuente.setUserDn("cn=admin,dc=sso,dc=local");
        fuente.setPassword("admin");
        fuente.afterPropertiesSet();
        return new LdapTemplate(fuente);
    }

    @Test
    void autenticaConLaClaveDelDirectorio() throws Exception {
        LdapTemplate ldap = comoIntegraAD();
        assertTrue(ldap.authenticate("ou=clientes", new EqualsFilter("uid", "ana").encode(), "1234"));
        assertFalse(ldap.authenticate("ou=clientes", new EqualsFilter("uid", "ana").encode(), "mala"));
        assertFalse(ldap.authenticate("ou=clientes", new EqualsFilter("uid", "nadie").encode(), "1234"));
    }

    @Test
    void atiendeBindPorLdaps() throws Exception {
        SSLUtil ssl = new SSLUtil(new TrustAllTrustManager());
        try (LDAPConnection c = new LDAPConnection(ssl.createSSLSocketFactory(), "localhost", 13636)) {
            assertEquals(ResultCode.SUCCESS, c.bind("uid=luis,ou=clientes,dc=sso,dc=local", "1234").getResultCode());
            try {
                c.bind("uid=luis,ou=clientes,dc=sso,dc=local", "otra");
                throw new AssertionError("debió rechazar la clave");
            } catch (LDAPException e) {
                assertEquals(ResultCode.INVALID_CREDENTIALS, e.getResultCode());
            }
        }
    }
}
