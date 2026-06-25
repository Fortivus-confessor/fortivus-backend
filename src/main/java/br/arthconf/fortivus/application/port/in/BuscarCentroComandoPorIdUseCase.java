package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.CentroComando;

import java.util.Optional;
import java.util.UUID;

public interface BuscarCentroComandoPorIdUseCase {
    Optional<CentroComando> executar(UUID id);
}
