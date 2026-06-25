package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.out.CentroComandoRepositoryPort;
import br.arthconf.fortivus.domain.model.CentroComando;
import br.arthconf.fortivus.infrastructure.persistence.entity.CentroComandoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.CentroComandoMapper;
import br.arthconf.fortivus.repository.CentroComandoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CentroComandoPersistenceAdapter implements CentroComandoRepositoryPort {

    private final CentroComandoRepository repository;

    @Override
    @Transactional
    public CentroComando salvar(CentroComando centroComando) {
        CentroComandoEntity entity;
        if (centroComando.getId() != null) {
            entity = repository.findById(centroComando.getId()).orElse(new CentroComandoEntity());
        } else {
            entity = new CentroComandoEntity();
        }
        entity.setId(centroComando.getId());
        entity.setNome(centroComando.getNome());
        entity.setEndereco(centroComando.getEndereco());
        entity.setTelefone(centroComando.getTelefone());
        entity.setCentral(centroComando.isCentral());
        if (centroComando.getLatitude() != null && centroComando.getLongitude() != null) {
            org.locationtech.jts.geom.GeometryFactory gf =
                    new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
            entity.setGeom(gf.createPoint(new org.locationtech.jts.geom.Coordinate(
                    centroComando.getLongitude(), centroComando.getLatitude())));
        }
        return CentroComandoMapper.toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CentroComando> buscarPorId(UUID id) {
        return repository.findById(id).map(CentroComandoMapper::toDomain);
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CentroComando> listarTodos() {
        return repository.findAllOrdered().stream()
                .map(CentroComandoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CentroComando> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable).map(CentroComandoMapper::toDomain);
    }
}
