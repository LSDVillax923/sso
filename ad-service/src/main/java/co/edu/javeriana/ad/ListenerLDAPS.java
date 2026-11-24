package co.edu.javeriana.ad;

import com.unboundid.ldap.listener.LDAPListener;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.listener.LDAPListenerConfig;
import com.unboundid.ldap.listener.LDAPListenerRequestHandler;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ssl.KeyStoreKeyManager;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.net.ServerSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Asset de entrada del AD: escucha LDAP 389 y LDAPS 636, atiende el bind de IntegraAD (y la búsqueda por uid que hace
 * LdapTemplate.authenticate antes del bind) y lo pasa a SRBAutenticacionLDAP. El directorio es de solo lectura.
 */
@Component
public class ListenerLDAPS extends LDAPListenerRequestHandler implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(ListenerLDAPS.class);

    private final int puerto;
    private final int puertoLdap;
    private final String keystore;
    private final String keystoreClave;
    private final SRBAutenticacionLDAP srbAutenticacionLDAP;
    private final IntegraDirectorio integraDirectorio;
    private final LDAPListenerClientConnection conexion;
    private final List<LDAPListener> escuchas = new ArrayList<>();

    @Autowired
    public ListenerLDAPS(@Value("${ad.puerto:636}") int puerto, @Value("${ad.puerto-ldap:389}") int puertoLdap,
                         @Value("${ad.keystore}") String keystore, @Value("${ad.keystore-clave}") String keystoreClave,
                         SRBAutenticacionLDAP srbAutenticacionLDAP, IntegraDirectorio integraDirectorio) {
        this.puerto = puerto;
        this.puertoLdap = puertoLdap;
        this.keystore = keystore;
        this.keystoreClave = keystoreClave;
        this.srbAutenticacionLDAP = srbAutenticacionLDAP;
        this.integraDirectorio = integraDirectorio;
        this.conexion = null;
    }

    /** Una instancia por conexión de cliente, con las mismas dependencias. */
    private ListenerLDAPS(ListenerLDAPS plantilla, LDAPListenerClientConnection conexion) {
        this.puerto = plantilla.puerto;
        this.puertoLdap = plantilla.puertoLdap;
        this.keystore = plantilla.keystore;
        this.keystoreClave = plantilla.keystoreClave;
        this.srbAutenticacionLDAP = plantilla.srbAutenticacionLDAP;
        this.integraDirectorio = plantilla.integraDirectorio;
        this.conexion = conexion;
    }

    @Override
    public LDAPListenerRequestHandler newInstance(LDAPListenerClientConnection conexion) {
        return new ListenerLDAPS(this, conexion);
    }

    public ResultadoBind recibirBind(String dn, String clave) {
        if ((dn == null || dn.isEmpty()) && (clave == null || clave.isEmpty())) {
            return ResultadoBind.EXITOSO; // bind anónimo, como lo permite slapd por defecto
        }
        ResultadoBind resultado = srbAutenticacionLDAP.autenticar(dn, clave);
        log.info("bind {} -> {}", dn, resultado);
        return resultado;
    }

    public List<UsuarioAD> buscar(String base, String filtro) {
        try {
            String uid = uidDelFiltro(Filter.create(filtro));
            UsuarioAD usuario = integraDirectorio.buscarPorUid(uid);
            if (usuario == null || !new DN(dnDe(usuario)).isDescendantOf(new DN(base), true)) {
                return List.of();
            }
            return List.of(usuario);
        } catch (LDAPException e) {
            return List.of();
        }
    }

    @Override
    public LDAPMessage processBindRequest(int idMensaje, BindRequestProtocolOp peticion, List<Control> controles) {
        if (peticion.getCredentialsType() != BindRequestProtocolOp.CRED_TYPE_SIMPLE) {
            return new LDAPMessage(idMensaje, new BindResponseProtocolOp(ResultCode.AUTH_METHOD_NOT_SUPPORTED_INT_VALUE,
                    null, "solo se admite bind simple", null, null));
        }
        String clave = peticion.getSimplePassword() == null ? "" : peticion.getSimplePassword().stringValue();
        ResultCode codigo = switch (recibirBind(peticion.getBindDN(), clave)) {
            case EXITOSO -> ResultCode.SUCCESS;
            case CREDENCIALES_INVALIDAS -> ResultCode.INVALID_CREDENTIALS;
            case NO_DISPONIBLE -> ResultCode.UNAVAILABLE;
        };
        return new LDAPMessage(idMensaje, new BindResponseProtocolOp(codigo.intValue(), null, null, null, null));
    }

    @Override
    public LDAPMessage processSearchRequest(int idMensaje, SearchRequestProtocolOp peticion, List<Control> controles) {
        for (UsuarioAD u : buscar(peticion.getBaseDN(), peticion.getFilter().toString())) {
            Entry entrada = new Entry(dnDe(u));
            entrada.addAttribute("objectClass", "top", "person", "organizationalPerson", "inetOrgPerson");
            entrada.addAttribute("uid", u.uid());
            entrada.addAttribute("cn", u.cn());
            if (u.mail() != null) entrada.addAttribute("mail", u.mail());
            if (u.memberOf() != null) entrada.addAttribute("memberOf", u.memberOf().split(";"));
            try {
                conexion.sendSearchResultEntry(idMensaje, entrada);
            } catch (LDAPException e) {
                return new LDAPMessage(idMensaje, new SearchResultDoneProtocolOp(e.getResultCode().intValue(), null, e.getMessage(), null));
            }
        }
        return new LDAPMessage(idMensaje, new SearchResultDoneProtocolOp(ResultCode.SUCCESS_INT_VALUE, null, null, null));
    }

    @Override
    public LDAPMessage processAddRequest(int idMensaje, AddRequestProtocolOp peticion, List<Control> controles) {
        return new LDAPMessage(idMensaje, new AddResponseProtocolOp(ResultCode.UNWILLING_TO_PERFORM_INT_VALUE, null, SOLO_LECTURA, null));
    }

    @Override
    public LDAPMessage processCompareRequest(int idMensaje, CompareRequestProtocolOp peticion, List<Control> controles) {
        return new LDAPMessage(idMensaje, new CompareResponseProtocolOp(ResultCode.UNWILLING_TO_PERFORM_INT_VALUE, null, SOLO_LECTURA, null));
    }

    @Override
    public LDAPMessage processDeleteRequest(int idMensaje, DeleteRequestProtocolOp peticion, List<Control> controles) {
        return new LDAPMessage(idMensaje, new DeleteResponseProtocolOp(ResultCode.UNWILLING_TO_PERFORM_INT_VALUE, null, SOLO_LECTURA, null));
    }

    @Override
    public LDAPMessage processExtendedRequest(int idMensaje, ExtendedRequestProtocolOp peticion, List<Control> controles) {
        return new LDAPMessage(idMensaje, new ExtendedResponseProtocolOp(ResultCode.UNWILLING_TO_PERFORM_INT_VALUE, null,
                "use LDAPS en el puerto " + puerto, null, null, null));
    }

    @Override
    public LDAPMessage processModifyRequest(int idMensaje, ModifyRequestProtocolOp peticion, List<Control> controles) {
        return new LDAPMessage(idMensaje, new ModifyResponseProtocolOp(ResultCode.UNWILLING_TO_PERFORM_INT_VALUE, null, SOLO_LECTURA, null));
    }

    @Override
    public LDAPMessage processModifyDNRequest(int idMensaje, ModifyDNRequestProtocolOp peticion, List<Control> controles) {
        return new LDAPMessage(idMensaje, new ModifyDNResponseProtocolOp(ResultCode.UNWILLING_TO_PERFORM_INT_VALUE, null, SOLO_LECTURA, null));
    }

    private static final String SOLO_LECTURA = "el directorio simulado es de solo lectura";

    private String dnDe(UsuarioAD u) {
        return "uid=" + u.uid() + ",ou=clientes," + integraDirectorio.getBase();
    }

    /** LdapTemplate busca con (uid=usuario); también se acepta dentro de un AND. */
    private static String uidDelFiltro(Filter f) {
        if (f.getFilterType() == Filter.FILTER_TYPE_EQUALITY && "uid".equalsIgnoreCase(f.getAttributeName())) {
            return f.getAssertionValue();
        }
        if (f.getFilterType() == Filter.FILTER_TYPE_AND) {
            for (Filter componente : f.getComponents()) {
                String uid = uidDelFiltro(componente);
                if (uid != null) return uid;
            }
        }
        return null;
    }

    /* ---------- ciclo de vida: abre LDAP y LDAPS al arrancar Spring ---------- */

    @Override
    public void start() {
        try {
            abrir(puertoLdap, null);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir LDAP en el puerto " + puertoLdap, e);
        }
        try {
            abrir(puerto, fabricaTLS());
        } catch (Exception e) {
            log.warn("LDAPS {} no quedó disponible ({}); IntegraAD puede usar ldap://...:{}", puerto, e.getMessage(), puertoLdap);
        }
    }

    private void abrir(int numero, ServerSocketFactory fabrica) throws IOException {
        if (numero <= 0) return;
        LDAPListenerConfig config = new LDAPListenerConfig(numero, this);
        if (fabrica != null) config.setServerSocketFactory(fabrica);
        LDAPListener escucha = new LDAPListener(config);
        escucha.startListening();
        escuchas.add(escucha);
        log.info("Active Directory simulado escuchando {} en el puerto {}", fabrica == null ? "LDAP" : "LDAPS", escucha.getListenPort());
    }

    /** Certificado propio del AD: se genera con keytool la primera vez (los clientes deben confiar en él). */
    private ServerSocketFactory fabricaTLS() throws Exception {
        Path archivo = Path.of(keystore);
        if (!Files.exists(archivo)) {
            String keytool = Path.of(System.getProperty("java.home"), "bin", "keytool").toString();
            Process p = new ProcessBuilder(keytool, "-genkeypair", "-alias", "ad", "-keyalg", "RSA", "-keysize", "2048",
                    "-validity", "3650", "-dname", "CN=ad.sso.local", "-storetype", "PKCS12", "-keystore", archivo.toString(),
                    "-storepass", keystoreClave, "-keypass", keystoreClave).redirectErrorStream(true).start();
            if (p.waitFor() != 0) {
                throw new IOException("keytool no pudo generar " + archivo + ": " + new String(p.getInputStream().readAllBytes()));
            }
        }
        SSLUtil ssl = new SSLUtil(new KeyStoreKeyManager(archivo.toFile(), keystoreClave.toCharArray(), "PKCS12", null),
                new TrustAllTrustManager());
        return ssl.createSSLServerSocketFactory();
    }

    @Override
    public void stop() {
        escuchas.forEach(e -> e.shutDown(true));
        escuchas.clear();
    }

    @Override
    public boolean isRunning() {
        return !escuchas.isEmpty();
    }
}
