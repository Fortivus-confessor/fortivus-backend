package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.RelatorioTerrestre;

import java.util.Optional;

public interface RelatorioTerrestreRepositoryPort {
    Optional<RelatorioTerrestre> buscarPorDespachoId(Long despachoId);
    RelatorioTerrestre salvar(RelatorioTerrestre relatorio);
}
