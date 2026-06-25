package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Equipamento;

import java.util.UUID;

public interface SalvarEquipamentoUseCase {
    record Command(UUID id, String nome, String identificador,
                   br.arthconf.fortivus.domain.EstadoEquipamento estado, UUID equipeId) {}
    Equipamento executar(Command command);
}
