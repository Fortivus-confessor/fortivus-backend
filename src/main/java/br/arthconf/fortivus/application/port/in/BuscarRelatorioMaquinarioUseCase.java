package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.dto.RelatorioMaquinarioDTO;
import java.util.Optional;

public interface BuscarRelatorioMaquinarioUseCase {
    Optional<RelatorioMaquinarioDTO> buscarPorDespachoId(Long despachoId);
}
