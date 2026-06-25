package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioMaquinarioEntity;

import java.util.Optional;

public interface RelatorioMaquinarioPort {
    RelatorioMaquinarioEntity salvar(RelatorioMaquinarioEntity relatorio);
    Optional<RelatorioMaquinarioEntity> buscarPorDespachoId(Long despachoId);
}
