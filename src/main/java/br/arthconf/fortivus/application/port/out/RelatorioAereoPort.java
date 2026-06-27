package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.RelatorioAereo;

import java.util.Optional;

public interface RelatorioAereoPort {
    RelatorioAereo salvar(RelatorioAereo relatorio);
    Optional<RelatorioAereo> buscarPorDespachoId(Long despachoId);
}
