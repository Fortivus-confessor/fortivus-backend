package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.output.DespachoRepositoryPort;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.DespachoMapper;
import br.arthconf.fortivus.repository.DespachoRepository;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import br.arthconf.fortivus.repository.EscalaRepository;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import br.arthconf.fortivus.infrastructure.persistence.entity.OrdemServicoEntity;

@Component
@RequiredArgsConstructor
public class DespachoPersistenceAdapter implements DespachoRepositoryPort {

    private final DespachoRepository despachoRepository;
    private final DespachoMapper mapper;
    private final OrdemServicoRepository ordemServicoRepository;
    private final EscalaRepository escalaRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    public Despacho salvar(Despacho despacho) {
        DespachoEntity entity = new DespachoEntity();
        
        if (despacho.getId() != null) {
            entity = despachoRepository.findById(despacho.getId()).orElse(new DespachoEntity());
            entity.setId(despacho.getId());
        } else {
            long anoAtual = java.time.LocalDateTime.now().getYear();
            long minId = anoAtual * 100000000L;
            long maxId = (anoAtual + 1) * 100000000L;
            Long maxExistente = despachoRepository.findMaxIdByAno(minId, maxId).orElse(minId);
            entity.setId(maxExistente == minId ? minId + 1 : maxExistente + 1);
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

        DespachoEntity salvo = despachoRepository.save(entity);
        return mapper.toDomain(salvo);
    }

    @Override
    public Optional<Despacho> buscarPorId(Long id) {
        return despachoRepository.findByIdFetched(id).map(mapper::toDomain);
    }

    @Override
    public void deletar(Long id) {
        despachoRepository.deleteById(id);
    }
}




