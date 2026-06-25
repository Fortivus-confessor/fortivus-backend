package br.arthconf.fortivus.application.port.output;

import br.arthconf.fortivus.domain.model.Equipamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipamentoRepositoryPort {
    Equipamento salvar(Equipamento equipamento);
    Optional<Equipamento> buscarPorId(UUID id);
    void deletar(UUID id);
    List<Equipamento> listarTodos();
}
