package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.dto.RelatorioMaquinarioDTO;

public interface SalvarRelatorioMaquinarioUseCase {
    RelatorioMaquinarioDTO salvar(Long despachoId, RelatorioMaquinarioDTO dto);
}
