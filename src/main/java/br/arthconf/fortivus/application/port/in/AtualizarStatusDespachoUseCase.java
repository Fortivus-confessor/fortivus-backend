package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.SituacaoDespacho;

public interface AtualizarStatusDespachoUseCase {
    void executar(Long id, SituacaoDespacho novoStatus);
}
