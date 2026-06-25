package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.Escala;
import br.arthconf.fortivus.infrastructure.persistence.entity.EscalaEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EscalaMapper {

    public static Escala toDomain(EscalaEntity entity) {
        if (entity == null) return null;

        List<UUID> integrantesIds = entity.getIntegrantes() != null
                ? entity.getIntegrantes().stream().map(u -> u.getId()).collect(Collectors.toList())
                : List.of();

        UUID centroId = null;
        if (entity.getEquipe() != null && entity.getEquipe().getCentroComando() != null) {
            centroId = entity.getEquipe().getCentroComando().getId();
        }

        return Escala.builder()
                .id(entity.getId())
                .equipeId(entity.getEquipe() != null ? entity.getEquipe().getId() : null)
                .equipeNome(entity.getEquipe() != null ? entity.getEquipe().getNome() : null)
                .centroComandoId(centroId)
                .veiculoId(entity.getVeiculo() != null ? entity.getVeiculo().getId() : null)
                .comandanteId(entity.getComandante() != null ? entity.getComandante().getId() : null)
                .comandanteNome(entity.getComandante() != null ? entity.getComandante().getNome() : null)
                .dataInicio(entity.getDataInicio())
                .dataFim(entity.getDataFim())
                .ativa(entity.isAtiva())
                .integrantesIds(integrantesIds)
                .build();
    }

    public static List<Escala> toDomainList(List<EscalaEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(EscalaMapper::toDomain).collect(Collectors.toList());
    }
}
