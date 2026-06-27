package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.out.DespachoRepositoryPort;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.OrdemServicoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.DespachoMapper;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataDespachoRepository;
import br.arthconf.fortivus.repository.EscalaRepository;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import br.arthconf.fortivus.repository.RelatorioTerrestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DespachoPersistenceAdapter implements DespachoRepositoryPort {

    private final SpringDataDespachoRepository despachoRepository;
    private final DespachoMapper mapper;
    private final OrdemServicoRepository ordemServicoRepository;
    private final EscalaRepository escalaRepository;
    private final SpringDataUsuarioRepository usuarioRepository;
    private final RelatorioTerrestreRepository relatorioTerrestreRepository;
    private final br.arthconf.fortivus.adapters.out.persistence.RelatorioMaquinarioRepository relatorioMaquinarioRepository;
    private final br.arthconf.fortivus.adapters.out.persistence.RelatorioAereoRepository relatorioAereoRepository;

    @Override
    @Transactional
    public Despacho salvar(Despacho despacho) {
        DespachoEntity entity;

        if (despacho.getId() != null) {
            entity = despachoRepository.findById(despacho.getId()).orElse(new DespachoEntity());
            entity.setId(despacho.getId());
        } else {
            entity = new DespachoEntity();
            long anoAtual = java.time.LocalDateTime.now().getYear();
            long minId = anoAtual * 100000000L;
            long maxId = (anoAtual + 1) * 100000000L;
            Long maxExistente = despachoRepository.findMaxIdByAno(minId, maxId).orElse(minId);
            entity.setId(maxExistente.equals(minId) ? minId + 1 : maxExistente + 1);
        }

        entity.setCategoria(despacho.getCategoria());
        entity.setLatitude(despacho.getLatitude());
        entity.setLongitude(despacho.getLongitude());
        entity.setDescricaoTarefa(despacho.getDescricaoTarefa());
        entity.setStatus(despacho.getStatus());
        entity.setDataInicio(despacho.getDataInicio());
        entity.setDataFim(despacho.getDataFim());

        if (despacho.getOrdemServicoId() != null) {
            OrdemServicoEntity os = ordemServicoRepository.findById(despacho.getOrdemServicoId()).orElse(null);
            if (despacho.getId() == null && os != null && os.getStatus() == br.arthconf.fortivus.domain.SituacaoOrdemServico.CONCLUIDA) {
                os.setStatus(br.arthconf.fortivus.domain.SituacaoOrdemServico.EM_EXECUCAO);
            }
            entity.setOrdemServico(os);
        }
        if (despacho.getEscalaId() != null) {
            entity.setEscala(escalaRepository.findById(despacho.getEscalaId()).orElse(null));
        }
        if (despacho.getResponsavelId() != null) {
            entity.setResponsavel(usuarioRepository.findById(despacho.getResponsavelId()).orElse(null));
        }

        return mapper.toDomain(despachoRepository.save(entity));
    }

    @Override
    public Optional<Despacho> buscarPorId(Long id) {
        return despachoRepository.findByIdFetched(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (relatorioTerrestreRepository.existsById(id)) {
            relatorioTerrestreRepository.deleteById(id);
        }
        if (relatorioMaquinarioRepository.existsById(id)) {
            relatorioMaquinarioRepository.deleteById(id);
        }
        if (relatorioAereoRepository.existsById(id)) {
            relatorioAereoRepository.deleteById(id);
        }
        despachoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Despacho> listarTodas() {
        return mapper.toDomainList(despachoRepository.findAllWithDetails());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Despacho> listarPorCentroComando(UUID centroId) {
        return mapper.toDomainList(despachoRepository.findAllByCentroComandoIdList(centroId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Despacho> listarPorCombatente(UUID usuarioId) {
        return mapper.toDomainList(despachoRepository.findAllByCombatenteIdList(usuarioId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Despacho> listarPaginado(Pageable pageable) {
        return despachoRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Despacho> listarPorCentroComandoPaginado(UUID centroId, Pageable pageable) {
        return despachoRepository.findAllByCentroComandoId(centroId, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Despacho> listarPorCombatentePaginado(UUID usuarioId, Pageable pageable) {
        return despachoRepository.findAllByCombatenteId(usuarioId, pageable).map(mapper::toDomain);
    }

    @Override
    public boolean pertenceAoDespacho(Long despachoId, UUID usuarioId) {
        return despachoRepository.pertenceAoDespacho(despachoId, usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Despacho> listarMeusPaginado(UUID responsavelId, List<br.arthconf.fortivus.domain.SituacaoDespacho> statuses, Pageable pageable) {
        if (statuses == null || statuses.isEmpty()) {
            return despachoRepository.findByResponsavelId(responsavelId, pageable).map(mapper::toDomain);
        }
        return despachoRepository.findByResponsavelIdAndStatusIn(responsavelId, statuses, pageable).map(mapper::toDomain);
    }
}
