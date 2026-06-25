package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.Equipe;
import br.arthconf.fortivus.infrastructure.persistence.entity.EquipeEntity;

import java.util.stream.Collectors;

public class EquipeMapper {

    public static Equipe toDomain(EquipeEntity entity) {
        if (entity == null) return null;
        Equipe domain = toDomainWithoutUsuarios(entity);
        if (entity.getUsuarios() != null) {
            domain.setUsuarios(entity.getUsuarios().stream()
                    .map(UsuarioMapper::toDomainWithoutEquipe)
                    .collect(Collectors.toList()));
        }
        return domain;
    }

    public static Equipe toDomainWithoutUsuarios(EquipeEntity entity) {
        if (entity == null) return null;
        return Equipe.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .categoria(entity.getCategoria())
                .centroComando(CentroComandoMapper.toDomain(entity.getCentroComando()))
                .build();
    }

    public static EquipeEntity toEntity(Equipe domain) {
        if (domain == null) return null;
        EquipeEntity entity = toEntityWithoutUsuarios(domain);
        if (domain.getUsuarios() != null) {
            entity.setUsuarios(domain.getUsuarios().stream()
                    .map(UsuarioMapper::toEntityWithoutEquipe)
                    .collect(Collectors.toList()));
        }
        return entity;
    }

    public static EquipeEntity toEntityWithoutUsuarios(Equipe domain) {
        if (domain == null) return null;
        EquipeEntity entity = new EquipeEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setCategoria(domain.getCategoria());
        entity.setCentroComando(CentroComandoMapper.toEntity(domain.getCentroComando()));
        return entity;
    }
}
