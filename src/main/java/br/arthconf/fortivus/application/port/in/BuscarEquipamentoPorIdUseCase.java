package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Equipamento;

import java.util.Optional;
import java.util.UUID;

public interface BuscarEquipamentoPorIdUseCase {
    Optional<Equipamento> executar(UUID id);
}
