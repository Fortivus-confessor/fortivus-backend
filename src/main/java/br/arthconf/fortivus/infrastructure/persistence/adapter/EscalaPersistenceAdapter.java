package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.out.EscalaRepositoryPort;
import br.arthconf.fortivus.domain.model.Escala;
import br.arthconf.fortivus.infrastructure.persistence.entity.EscalaEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.UsuarioEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.EscalaMapper;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataEquipeRepository;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataVeiculoRepository;
import br.arthconf.fortivus.repository.EscalaRepository;
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
public class EscalaPersistenceAdapter implements EscalaRepositoryPort {

    private final EscalaRepository escalaRepository;
    private final SpringDataEquipeRepository equipeRepository;
    private final SpringDataVeiculoRepository veiculoRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Escala salvar(Escala escala, List<UUID> integrantesIds) {
        EscalaEntity entity;
        if (escala.getId() != null) {
            entity = escalaRepository.findById(escala.getId()).orElse(new EscalaEntity());
        } else {
            entity = new EscalaEntity();
        }

        if (escala.getEquipeId() != null) {
            entity.setEquipe(equipeRepository.findById(escala.getEquipeId()).orElse(null));
        }
        if (escala.getVeiculoId() != null) {
            entity.setVeiculo(veiculoRepository.findById(escala.getVeiculoId()).orElse(null));
        }
        if (escala.getComandanteId() != null) {
            entity.setComandante(usuarioRepository.findById(escala.getComandanteId()).orElse(null));
        }
        entity.setDataInicio(escala.getDataInicio());
        entity.setDataFim(escala.getDataFim());
        entity.setAtiva(escala.isAtiva());

        if (integrantesIds != null) {
            List<UsuarioEntity> integrantes = usuarioRepository.findAllById(integrantesIds);
            entity.setIntegrantes(integrantes);
        }

        return EscalaMapper.toDomain(escalaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Escala> buscarPorId(UUID id) {
        return escalaRepository.findByIdFetched(id).map(e -> {
            e.getIntegrantes().size(); // inicializa lazy
            return EscalaMapper.toDomain(e);
        });
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        escalaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Escala> listarTodas() {
        List<EscalaEntity> lista = escalaRepository.findAllFetched();
        lista.forEach(e -> e.getIntegrantes().size());
        return EscalaMapper.toDomainList(lista);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Escala> listarAtivas() {
        List<EscalaEntity> lista = escalaRepository.findAtivas();
        lista.forEach(e -> e.getIntegrantes().size());
        return EscalaMapper.toDomainList(lista);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Escala> listarPorCentroComando(UUID centroId) {
        List<EscalaEntity> lista = escalaRepository.findAllByCentroComandoIdList(centroId);
        lista.forEach(e -> e.getIntegrantes().size());
        return EscalaMapper.toDomainList(lista);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Escala> listarPaginado(Pageable pageable) {
        return escalaRepository.findAll(pageable).map(EscalaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Escala> listarPorCentroComandoPaginado(UUID centroId, Pageable pageable) {
        return escalaRepository.findAllByCentroComandoId(centroId, pageable).map(EscalaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public EscalaEntity buscarEntidadePorId(UUID id) {
        return escalaRepository.findByIdFetched(id)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada: " + id));
    }
}
