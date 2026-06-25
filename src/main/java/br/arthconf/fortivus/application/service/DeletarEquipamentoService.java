package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.DeletarEquipamentoUseCase;
import br.arthconf.fortivus.application.port.output.EquipamentoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletarEquipamentoService implements DeletarEquipamentoUseCase {

    private final EquipamentoRepositoryPort equipamentoPort;

    @Override
    @Transactional
    public void executar(UUID id) {
        equipamentoPort.deletar(id);
    }
}
