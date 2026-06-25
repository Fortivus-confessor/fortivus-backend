package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.CheckoutEquipamento;
import br.arthconf.fortivus.infrastructure.persistence.entity.CheckoutEquipamentoEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CheckoutEquipamentoMapper {

    public static CheckoutEquipamento toDomain(CheckoutEquipamentoEntity entity) {
        if (entity == null) return null;
        return CheckoutEquipamento.builder()
                .id(entity.getId())
                .escalaId(entity.getEscala() != null ? entity.getEscala().getId() : null)
                .equipamentoId(entity.getEquipamento() != null ? entity.getEquipamento().getId() : null)
                .responsavelEntregaId(entity.getResponsavelEntrega() != null ? entity.getResponsavelEntrega().getId() : null)
                .dataEmprestimo(entity.getDataEmprestimo())
                .dataDevolucao(entity.getDataDevolucao())
                .responsavelRecebimentoId(entity.getResponsavelRecebimento() != null ? entity.getResponsavelRecebimento().getId() : null)
                .build();
    }

    public static List<CheckoutEquipamento> toDomainList(List<CheckoutEquipamentoEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(CheckoutEquipamentoMapper::toDomain).collect(Collectors.toList());
    }
}
