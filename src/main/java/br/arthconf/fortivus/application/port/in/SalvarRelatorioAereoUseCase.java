package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.dto.RelatorioAereoDTO;

public interface SalvarRelatorioAereoUseCase {
    RelatorioAereoDTO salvar(Long despachoId, RelatorioAereoDTO dto);
}
