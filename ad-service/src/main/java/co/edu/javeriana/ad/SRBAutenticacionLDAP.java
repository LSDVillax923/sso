package co.edu.javeriana.ad;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Bind simple: busca al usuario del DN en el directorio y compara la clave (texto plano, {SHA} o {SSHA}). */
@Service
public class SRBAutenticacionLDAP {

    private static final Logger log = LoggerFactory.getLogger(SRBAutenticacionLDAP.class);

    private final IntegraDirectorio integraDirectorio;
    private final String adminDn;
    private final String adminClave;

    public SRBAutenticacionLDAP(IntegraDirectorio integraDirectorio, @Value("${ad.admin-dn}") String adminDn,
                                @Value("${ad.admin-clave}") String adminClave) {
        this.integraDirectorio = integraDirectorio;
        this.adminDn = adminDn;
        this.adminClave = adminClave;
    }

    public ResultadoBind autenticar(String dn, String clave) {
        if (clave == null || clave.isEmpty()) {
            // un DN sin clave es un bind "no autenticado": no se acepta
            return ResultadoBind.CREDENCIALES_INVALIDAS;
        }
        try {
            DN dnBind = new DN(dn);
            // el administrador es el rootdn del directorio, como en slapd: no vive en la base
            if (dnBind.equals(new DN(adminDn))) {
                return adminClave.equals(clave) ? ResultadoBind.EXITOSO : ResultadoBind.CREDENCIALES_INVALIDAS;
            }
            String uid = dnBind.getRDN().getAttributeValues()[0];
            UsuarioAD usuario = integraDirectorio.buscarPorUid(uid);
            return usuario != null && verificarClave(usuario, clave) ? ResultadoBind.EXITOSO : ResultadoBind.CREDENCIALES_INVALIDAS;
        } catch (LDAPException e) {
            return ResultadoBind.CREDENCIALES_INVALIDAS;
        } catch (RuntimeException e) {
            log.error("No se pudo leer bd_directorio", e);
            return ResultadoBind.NO_DISPONIBLE;
        }
    }

    private boolean verificarClave(UsuarioAD u, String clave) {
        String guardada = u.userPassword();
        if (guardada == null) {
            return false;
        }
        byte[] dada = clave.getBytes(StandardCharsets.UTF_8);
        if (guardada.startsWith("{SSHA}") || guardada.startsWith("{SHA}")) {
            boolean conSal = guardada.startsWith("{SSHA}");
            byte[] bytes = Base64.getDecoder().decode(guardada.substring(conSal ? 6 : 5));
            byte[] hash = Arrays.copyOf(bytes, 20);
            byte[] sal = conSal ? Arrays.copyOfRange(bytes, 20, bytes.length) : new byte[0];
            try {
                MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                sha1.update(dada);
                sha1.update(sal);
                return MessageDigest.isEqual(hash, sha1.digest());
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
        return MessageDigest.isEqual(guardada.getBytes(StandardCharsets.UTF_8), dada);
    }
}
