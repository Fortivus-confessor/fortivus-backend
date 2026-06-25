package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.Veiculo;
import br.arthconf.fortivus.infrastructure.persistence.entity.VeiculoEntity;

public class VeiculoMapper {

    public static Veiculo toDomain(VeiculoEntity entity) {
        if (entity == null) return null;
        return Veiculo.builder()
                .id(entity.getId())
                .identificador(entity.getIdentificador())
                .prefixo(entity.getPrefixo())
                .modelo(entity.getModelo())
                .categoria(entity.getCategoria())
                .kmAtual(entity.getKmAtual())
                .contrato(entity.getContrato())
                .fotoUrl(entity.getFotoUrl())
                .equipe(EquipeMapper.toDomainWithoutUsuarios(entity.getEquipe()))
                .centroComando(CentroComandoMapper.toDomain(entity.getCentroComando()))
                .build();
    }

    public static VeiculoEntity toEntity(Veiculo domain) {
        if (domain == null) return null;
        VeiculoEntity entity = new VeiculoEntity();
        entity.setId(domain.getId());
        entity.setIdentificador(domain.getIdentificador());
        entity.setPrefixo(domain.getPrefixo());
        entity.setModelo(domain.getModelo());
        entity.setCategoria(domain.getCategoria());
        entity.setKmAtual(domain.getKmAtual());
        entity.setContrato(domain.getContrato());
        entity.setFotoUrl(domain.getFotoUrl());
        entity.setEquipe(EquipeMapper.toEntityWithoutUsuarios(domain.getEquipe()));
        entity.setCentroComando(CentroComandoMapper.toEntity(domain.getCentroComando()));
        return entity;
    }
}
