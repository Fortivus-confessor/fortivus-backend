package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.Equipamento;
import br.arthconf.fortivus.infrastructure.persistence.entity.EquipamentoEntity;

public class EquipamentoMapper {

    public static Equipamento toDomain(EquipamentoEntity entity) {
        if (entity == null) return null;
        return Equipamento.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .identificador(entity.getIdentificador())
                .estado(entity.getEstado())
                .equipeId(entity.getEquipe() != null ? entity.getEquipe().getId() : null)
                .build();
    }

    public static EquipamentoEntity toEntity(Equipamento domain) {
        if (domain == null) return null;
        EquipamentoEntity entity = new EquipamentoEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setIdentificador(domain.getIdentificador());
        entity.setEstado(domain.getEstado());
        return entity;
    }
}
