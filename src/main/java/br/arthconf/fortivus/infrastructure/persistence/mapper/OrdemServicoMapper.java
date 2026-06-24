package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.OrdemServico;
import br.arthconf.fortivus.infrastructure.persistence.entity.OrdemServicoEntity;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoMapper {

    public OrdemServico toDomain(OrdemServicoEntity entity) {
        if (entity == null) {
            return null;
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
                .build();
    }

    public OrdemServicoEntity toEntity(OrdemServico domain) {
        if (domain == null) {
            return null;
        }
        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setDescricaoTarefa(domain.getDescricaoTarefa());
        // Relacionamentos são injetados externamente quando necessário
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setDataFim(domain.getDataFim());
        entity.setStatus(domain.getStatus());
        entity.setEventoFogoId(domain.getEventoFogoId());
        return entity;
    }
}
