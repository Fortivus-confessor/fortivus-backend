package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.OrdemServico;

public interface EditarOrdemServicoUseCase {

    record Command(Long id, String descricaoTarefa, Long eventoFogoId) {}

    OrdemServico executar(Command command);
}
