package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Escala;

import java.util.Optional;
import java.util.UUID;

public interface BuscarEscalaPorIdUseCase {
    Optional<Escala> executar(UUID id);
}
