package co.edu.javeriana.ad;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/** Única salida a bd_directorio: la base del directorio (en OpenLDAP es back-mdb; aquí el mismo LDIF de infra/ad). */
@Component
public class IntegraDirectorio {

    private static final Logger log = LoggerFactory.getLogger(IntegraDirectorio.class);

    private final String base;
    private final Map<String, UsuarioAD> usuarios = new LinkedHashMap<>();
    private final List<GrupoAD> grupos = new ArrayList<>();

    public IntegraDirectorio(ResourceLoader cargador, @Value("${ad.base:dc=sso,dc=local}") String base,
                             @Value("${ad.base-datos}") String baseDatos) throws IOException, LDIFException {
        this.base = base;
        Map<String, String> dnDeGrupo = new LinkedHashMap<>();
        List<Entry> personas = new ArrayList<>();
        try (InputStream entrada = cargador.getResource(baseDatos).getInputStream();
             LDIFReader lector = new LDIFReader(entrada)) {
            for (Entry e = lector.readEntry(); e != null; e = lector.readEntry()) {
                if (e.hasObjectClass("groupOfNames")) {
                    List<String> miembros = e.hasAttribute("member") ? List.of(e.getAttributeValues("member")) : List.of();
                    grupos.add(new GrupoAD(e.getAttributeValue("cn"), miembros));
                    dnDeGrupo.put(e.getAttributeValue("cn"), e.getDN());
                } else if (e.hasObjectClass("inetOrgPerson")) {
                    personas.add(e);
                }
            }
        }
        for (Entry p : personas) {
            String memberOf = grupos.stream()
                    .filter(g -> g.member().stream().anyMatch(m -> m.equalsIgnoreCase(p.getDN())))
                    .map(g -> dnDeGrupo.get(g.cn()))
                    .reduce((a, b) -> a + ";" + b).orElse(null);
            String uid = p.getAttributeValue("uid");
            usuarios.put(uid.toLowerCase(), new UsuarioAD(uid, p.getAttributeValue("cn"), p.getAttributeValue("mail"),
                    p.getAttributeValue("userPassword"), memberOf));
        }
        log.info("bd_directorio {}: {} usuarios y {} grupos bajo {}", baseDatos, usuarios.size(), grupos.size(), base);
    }

    public String getBase() {
        return base;
    }

    public UsuarioAD buscarPorUid(String uid) {
        return uid == null ? null : usuarios.get(uid.toLowerCase());
    }

    public List<GrupoAD> gruposDe(String uid) {
        String prefijo = "uid=" + uid.toLowerCase() + ",";
        return grupos.stream().filter(g -> g.member().stream().anyMatch(m -> m.toLowerCase().startsWith(prefijo))).toList();
    }
}
