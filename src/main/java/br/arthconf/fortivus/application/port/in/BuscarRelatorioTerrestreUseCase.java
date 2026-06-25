package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioTerrestreEntity;

import java.util.Optional;

public interface BuscarRelatorioTerrestreUseCase {
    Optional<RelatorioTerrestreEntity> executar(Long despachoId);
}
