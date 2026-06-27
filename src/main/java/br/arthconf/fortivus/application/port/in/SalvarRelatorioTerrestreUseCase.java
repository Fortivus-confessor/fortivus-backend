package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.RelatorioTerrestre;

public interface SalvarRelatorioTerrestreUseCase {
    RelatorioTerrestre executar(RelatorioTerrestre relatorio);
}
