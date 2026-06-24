package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.out.EquipePort;
import br.arthconf.fortivus.domain.model.Equipe;
import br.arthconf.fortivus.infrastructure.persistence.entity.EquipeEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.EquipeMapper;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataEquipeRepository;
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
public class EquipePersistenceAdapter implements EquipePort {

    private final SpringDataEquipeRepository repository;

    @Override
    public List<Equipe> findAllComCentro() {
        return repository.findAllComCentro().stream()
                .map(EquipeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Equipe> findByCentroComandoId(UUID centroId) {
        return repository.findByCentroComandoId(centroId).stream()
                .map(EquipeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Equipe> findByCentroComandoId(UUID centroId, Pageable pageable) {
        return repository.findByCentroComandoId(centroId, pageable).map(EquipeMapper::toDomain);
    }

    @Override
    public Page<Equipe> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(EquipeMapper::toDomain);
    }

    @Override
    public Equipe save(Equipe equipe) {
        EquipeEntity entity = EquipeMapper.toEntity(equipe);
        EquipeEntity saved = repository.save(entity);
        return EquipeMapper.toDomain(saved);
    }

    @Override
    public Optional<Equipe> findById(UUID id) {
        return repository.findById(id).map(EquipeMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }
}
