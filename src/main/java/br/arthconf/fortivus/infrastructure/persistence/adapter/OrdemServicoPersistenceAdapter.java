package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.output.OrdemServicoRepositoryPort;
import br.arthconf.fortivus.domain.model.OrdemServico;
import br.arthconf.fortivus.infrastructure.persistence.entity.OrdemServicoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.OrdemServicoMapper;
import br.arthconf.fortivus.repository.EscalaRepository;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrdemServicoPersistenceAdapter implements OrdemServicoRepositoryPort {

    private final OrdemServicoRepository repository;
    private final OrdemServicoMapper mapper;
    private final EscalaRepository escalaRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public OrdemServico salvar(OrdemServico domain) {
        OrdemServicoEntity entity;
        if (domain.getId() != null) {
            entity = repository.findById(domain.getId()).orElse(new OrdemServicoEntity());
        } else {
            entity = new OrdemServicoEntity();
        }

        entity.setId(domain.getId());
        entity.setDescricaoTarefa(domain.getDescricaoTarefa());
        if (entity.getDataCriacao() == null) {
            entity.setDataCriacao(domain.getDataCriacao() != null ? domain.getDataCriacao() : LocalDateTime.now());
        }
        entity.setDataFim(domain.getDataFim());
        entity.setStatus(domain.getStatus());
        entity.setEventoFogoId(domain.getEventoFogoId());

        if (domain.getEscalaId() != null) {
            entity.setEscala(escalaRepository.findById(domain.getEscalaId()).orElse(null));
        }
        if (domain.getRelatorId() != null) {
            entity.setRelator(usuarioRepository.findById(domain.getRelatorId()).orElse(null));
        }

        return mapper.toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdemServico> buscarPorId(Long id) {
        return repository.findByIdFetched(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existe(Long id) {
        return repository.existsById(id);
    }

    @Override
    public boolean temDespachos(Long osId) {
        return repository.temDespachos(osId);
    }

    @Override
    public Optional<Long> findMaxId(Long minId, Long maxId) {
        return repository.findMaxIdByAno(minId, maxId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodas() {
        return mapper.toDomainList(repository.findAllFetched());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarPorCentroComando(UUID centroId) {
        return mapper.toDomainList(repository.findAllByCentroComandoIdList(centroId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarPorCombatente(UUID usuarioId) {
        return mapper.toDomainList(repository.findAllByCombatenteIdList(usuarioId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServico> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServico> listarPorCentroComandoPaginado(UUID centroId, Pageable pageable) {
        return repository.findAllByCentroComandoId(centroId, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServico> listarPorCombatentePaginado(UUID usuarioId, Pageable pageable) {
        return repository.findAllByCombatenteId(usuarioId, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarEmExecucaoComDespachos() {
        return repository.findAllEmExecucaoComDespachos().stream()
                .map(mapper::toDomainComDespachos)
                .toList();
    }
}
