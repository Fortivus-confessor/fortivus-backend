package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.RelatorioMaquinario;

import java.util.Optional;

public interface RelatorioMaquinarioPort {
    RelatorioMaquinario salvar(RelatorioMaquinario relatorio);
    Optional<RelatorioMaquinario> buscarPorDespachoId(Long despachoId);
}
