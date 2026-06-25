package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.RelatorioTerrestre;

public interface SalvarRelatorioTerrestreUseCase {
    RelatorioTerrestre executar(RelatorioTerrestre relatorio);
}
