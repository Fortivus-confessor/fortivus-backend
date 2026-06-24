package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Despacho;

import java.util.Optional;

public interface BuscarDespachoPorIdUseCase {
    Optional<Despacho> executar(Long id);
}
