package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioAereoEntity;

import java.util.Optional;

public interface RelatorioAereoPort {
    RelatorioAereoEntity salvar(RelatorioAereoEntity relatorio);
    Optional<RelatorioAereoEntity> buscarPorDespachoId(Long despachoId);
}
