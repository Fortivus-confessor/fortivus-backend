package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioTerrestreEntity;

public interface SalvarRelatorioTerrestreUseCase {
    RelatorioTerrestreEntity executar(RelatorioTerrestreEntity relatorio);
}
