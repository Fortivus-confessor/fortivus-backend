package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.out.EquipamentoRepositoryPort;
import br.arthconf.fortivus.domain.model.Equipamento;
import br.arthconf.fortivus.infrastructure.persistence.entity.EquipamentoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.EquipamentoMapper;
import br.arthconf.fortivus.repository.EquipamentoRepository;
import br.arthconf.fortivus.repository.EscalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EquipamentoPersistenceAdapter implements EquipamentoRepositoryPort {

    private final EquipamentoRepository equipamentoRepository;
    private final br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataEquipeRepository equipeRepository;

    @Override
    @Transactional
    public Equipamento salvar(Equipamento domain) {
        EquipamentoEntity entity;
        if (domain.getId() != null) {
            entity = equipamentoRepository.findById(domain.getId()).orElse(new EquipamentoEntity());
        } else {
            entity = new EquipamentoEntity();
        }
        entity.setNome(domain.getNome());
        entity.setIdentificador(domain.getIdentificador());
        entity.setEstado(domain.getEstado());
        if (domain.getEquipeId() != null) {
            entity.setEquipe(equipeRepository.findById(domain.getEquipeId()).orElse(null));
        } else {
            entity.setEquipe(null);
        }
        return EquipamentoMapper.toDomain(equipamentoRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Equipamento> buscarPorId(UUID id) {
        return equipamentoRepository.findById(id).map(EquipamentoMapper::toDomain);
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        equipamentoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipamento> listarTodos() {
        return equipamentoRepository.findAllWithEquipe().stream()
                .map(EquipamentoMapper::toDomain)
                .collect(Collectors.toList());
    }
}
