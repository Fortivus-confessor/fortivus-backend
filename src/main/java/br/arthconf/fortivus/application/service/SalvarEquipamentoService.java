package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.SalvarEquipamentoUseCase;
import br.arthconf.fortivus.application.port.out.EquipamentoRepositoryPort;
import br.arthconf.fortivus.domain.EstadoEquipamento;
import br.arthconf.fortivus.domain.model.Equipamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalvarEquipamentoService implements SalvarEquipamentoUseCase {

    private final EquipamentoRepositoryPort equipamentoPort;

    @Override
    @Transactional
    public Equipamento executar(Command cmd) {
        Equipamento equipamento = equipamentoPort.buscarPorId(cmd.id())
                .orElse(Equipamento.builder().build());
        equipamento.setNome(cmd.nome());
        equipamento.setIdentificador(cmd.identificador());
        equipamento.setEstado(cmd.estado() != null ? cmd.estado() : EstadoEquipamento.OPERANTE);
        equipamento.setEquipeId(cmd.equipeId());
        return equipamentoPort.salvar(equipamento);
    }
}
