package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.dto.RelatorioAereoDTO;
import java.util.Optional;

public interface BuscarRelatorioAereoUseCase {
    Optional<RelatorioAereoDTO> buscarPorDespachoId(Long despachoId);
}
