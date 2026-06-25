package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Veiculo;

import java.util.UUID;

public interface GerenciarVeiculoUseCase {
    Veiculo buscarPorId(UUID id);
    Veiculo salvar(Veiculo veiculo);
    void deletar(UUID id);
}
