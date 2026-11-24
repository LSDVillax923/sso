package co.edu.javeriana.sso.catalogo;

import co.edu.javeriana.sso.catalogo.BienDTO;
import co.edu.javeriana.sso.dominio.integra.IntegraBDSubasta;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrvCatalogo {

    private final IntegraBDSubasta integraBDSubasta;

    public SrvCatalogo(IntegraBDSubasta integraBDSubasta) {
        this.integraBDSubasta = integraBDSubasta;
    }

    @Transactional(readOnly = true)
    public List<BienDTO> buscarBienesEnSubasta(String filtro) {
        return integraBDSubasta.buscarAbiertas(filtro, LocalDateTime.now()).stream()
                .map(s -> new BienDTO(s.getIdSubasta(), s.getBien().getIdBien(), s.getBien().getNombre(),
                        s.getBien().getDescripcion(), s.getBien().getValorMinimo(), s.getFechaCierre()))
                .toList();
    }
}
