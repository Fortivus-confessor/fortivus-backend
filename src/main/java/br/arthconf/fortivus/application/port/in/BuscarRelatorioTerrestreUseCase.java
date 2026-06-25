package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.RelatorioTerrestre;

import java.util.Optional;

public interface BuscarRelatorioTerrestreUseCase {
    Optional<RelatorioTerrestre> executar(Long despachoId);
}
