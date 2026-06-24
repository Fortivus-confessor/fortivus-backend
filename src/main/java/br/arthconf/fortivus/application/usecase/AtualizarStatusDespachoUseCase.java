package br.arthconf.fortivus.application.usecase;

import br.arthconf.fortivus.domain.SituacaoDespacho;

public interface AtualizarStatusDespachoUseCase {
    void executar(Long id, SituacaoDespacho novoStatus);
}
