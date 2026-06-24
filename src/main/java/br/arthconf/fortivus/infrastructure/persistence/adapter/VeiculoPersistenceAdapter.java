package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.out.VeiculoPort;
import br.arthconf.fortivus.domain.model.Veiculo;
import br.arthconf.fortivus.infrastructure.persistence.entity.VeiculoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.VeiculoMapper;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataVeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VeiculoPersistenceAdapter implements VeiculoPort {

    private final SpringDataVeiculoRepository repository;

    @Override
    public List<Veiculo> findAllFetched() {
        return repository.findAllFetched().stream()
                .map(VeiculoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Veiculo save(Veiculo veiculo) {
        VeiculoEntity entity = VeiculoMapper.toEntity(veiculo);
        VeiculoEntity saved = repository.save(entity);
        return VeiculoMapper.toDomain(saved);
    }

    @Override
    public Optional<Veiculo> findById(UUID id) {
        return repository.findById(id).map(VeiculoMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<Veiculo> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(VeiculoMapper::toDomain);
    }
}
