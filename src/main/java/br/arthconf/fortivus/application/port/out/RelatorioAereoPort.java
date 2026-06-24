package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.RelatorioAereo;
import java.util.Optional;

public interface RelatorioAereoPort {
    RelatorioAereo salvar(RelatorioAereo relatorio);
    Optional<RelatorioAereo> buscarPorDespachoId(Long despachoId);
}
