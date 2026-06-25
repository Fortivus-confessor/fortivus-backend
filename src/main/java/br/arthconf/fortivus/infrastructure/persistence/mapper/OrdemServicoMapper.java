package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.domain.model.OrdemServico;
import br.arthconf.fortivus.infrastructure.persistence.entity.OrdemServicoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrdemServicoMapper {

    private final DespachoMapper despachoMapper;

    public OrdemServico toDomain(OrdemServicoEntity entity) {
        if (entity == null) return null;

        String tipoDespacho = null;
        if (entity.getDespachos() != null && !entity.getDespachos().isEmpty()) {
            var primeiro = entity.getDespachos().get(0);
            if (primeiro.getCategoria() != null) {
                tipoDespacho = primeiro.getCategoria().name();
            }
        }

        java.util.UUID centroComandoId = null;
        if (entity.getEscala() != null
                && entity.getEscala().getEquipe() != null
                && entity.getEscala().getEquipe().getCentroComando() != null) {
            centroComandoId = entity.getEscala().getEquipe().getCentroComando().getId();
        }

        return OrdemServico.builder()
                .id(entity.getId())
                .descricaoTarefa(entity.getDescricaoTarefa())
                .escalaId(entity.getEscala() != null ? entity.getEscala().getId() : null)
                .relatorId(entity.getRelator() != null ? entity.getRelator().getId() : null)
                .dataCriacao(entity.getDataCriacao())
                .dataFim(entity.getDataFim())
                .status(entity.getStatus())
                .eventoFogoId(entity.getEventoFogoId())
                .tipoDespacho(tipoDespacho)
                .centroComandoId(centroComandoId)
                .build();
    }

    public OrdemServico toDomainComDespachos(OrdemServicoEntity entity) {
        if (entity == null) return null;
        OrdemServico os = toDomain(entity);
        if (entity.getDespachos() != null) {
            List<Despacho> despachos = entity.getDespachos().stream()
                    .map(despachoMapper::toDomain)
                    .collect(Collectors.toList());
            os.setDespachos(despachos);
        }
        return os;
    }

    public List<OrdemServico> toDomainList(List<OrdemServicoEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    public OrdemServicoEntity toEntity(OrdemServico domain) {
        if (domain == null) return null;
        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setDescricaoTarefa(domain.getDescricaoTarefa());
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setDataFim(domain.getDataFim());
        entity.setStatus(domain.getStatus());
        entity.setEventoFogoId(domain.getEventoFogoId());
        return entity;
    }
}
