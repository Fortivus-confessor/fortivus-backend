package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioTerrestreEntity;

import java.util.Optional;

public interface RelatorioTerrestreRepositoryPort {
    Optional<RelatorioTerrestreEntity> buscarPorDespachoId(Long despachoId);
    RelatorioTerrestreEntity salvar(RelatorioTerrestreEntity relatorio);
}
