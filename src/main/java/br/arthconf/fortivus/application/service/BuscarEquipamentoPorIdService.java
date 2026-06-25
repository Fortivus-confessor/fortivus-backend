package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarEquipamentoPorIdUseCase;
import br.arthconf.fortivus.application.port.output.EquipamentoRepositoryPort;
import br.arthconf.fortivus.domain.model.Equipamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarEquipamentoPorIdService implements BuscarEquipamentoPorIdUseCase {

    private final EquipamentoRepositoryPort equipamentoPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<Equipamento> executar(UUID id) {
        return equipamentoPort.buscarPorId(id);
    }
}
