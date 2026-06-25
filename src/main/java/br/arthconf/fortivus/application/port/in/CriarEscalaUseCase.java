package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Escala;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CriarEscalaUseCase {
    record Command(UUID id, UUID equipeId, UUID veiculoId, UUID comandanteId,
                   LocalDateTime dataInicio, LocalDateTime dataFim, List<UUID> integrantesIds) {}
    Escala executar(Command command);
}
