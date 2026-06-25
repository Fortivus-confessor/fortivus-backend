package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Despacho;

public interface AtualizarDespachoUseCase {
    Despacho executar(Long id, Despacho atualizado);
}
