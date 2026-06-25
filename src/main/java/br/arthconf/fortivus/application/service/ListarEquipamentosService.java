package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.ListarEquipamentosUseCase;
import br.arthconf.fortivus.application.port.out.EquipamentoRepositoryPort;
import br.arthconf.fortivus.domain.model.Equipamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarEquipamentosService implements ListarEquipamentosUseCase {

    private final EquipamentoRepositoryPort equipamentoPort;

    @Override
    @Transactional(readOnly = true)
    public List<Equipamento> executar() {
        return equipamentoPort.listarTodos();
    }
}
