package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DespachoMapper {

    public Despacho toDomain(DespachoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Despacho.builder()
                .id(entity.getId())
                .ordemServicoId(entity.getOrdemServico() != null ? entity.getOrdemServico().getId() : null)
                .escalaId(entity.getEscala() != null ? entity.getEscala().getId() : null)
                .responsavelId(entity.getResponsavel() != null ? entity.getResponsavel().getId() : null)
                .categoria(entity.getCategoria())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .descricaoTarefa(entity.getDescricaoTarefa())
                .status(entity.getStatus())
                .dataInicio(entity.getDataInicio())
                .dataFim(entity.getDataFim())
                .build();
    }

    public List<Despacho> toDomainList(List<DespachoEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
