package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Equipamento;

import java.util.List;

public interface ListarEquipamentosUseCase {
    List<Equipamento> executar();
}
