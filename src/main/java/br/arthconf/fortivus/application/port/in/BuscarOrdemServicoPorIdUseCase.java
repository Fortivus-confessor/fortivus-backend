package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.OrdemServico;

import java.util.Optional;

public interface BuscarOrdemServicoPorIdUseCase {
    Optional<OrdemServico> executar(Long id);
}
