package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.infrastructure.persistence.entity.UsuarioEntity;

public class UsuarioMapper {

    public static Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        Usuario domain = toDomainWithoutEquipe(entity);
        domain.setEquipe(EquipeMapper.toDomainWithoutUsuarios(entity.getEquipe()));
        return domain;
    }

    public static Usuario toDomainWithoutEquipe(UsuarioEntity entity) {
        if (entity == null) return null;
        return Usuario.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .primeiroNome(entity.getPrimeiroNome())
                .email(entity.getEmail())
                .cpf(entity.getCpf())
                .rg(entity.getRg())
                .matricula(entity.getMatricula())
                .posto(entity.getPosto())
                .dataNascimento(entity.getDataNascimento())
                .tipoSanguineo(entity.getTipoSanguineo())
                .fotoUrl(entity.getFotoUrl())
                .senha(entity.getSenha())
                .perfil(entity.getPerfil())
                .estadoOperacional(entity.getEstadoOperacional())
                .centroComando(CentroComandoMapper.toDomain(entity.getCentroComando()))
                .build();
    }

    public static UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        UsuarioEntity entity = toEntityWithoutEquipe(domain);
        entity.setEquipe(EquipeMapper.toEntityWithoutUsuarios(domain.getEquipe()));
        return entity;
    }

    public static UsuarioEntity toEntityWithoutEquipe(Usuario domain) {
        if (domain == null) return null;
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setPrimeiroNome(domain.getPrimeiroNome());
        entity.setEmail(domain.getEmail());
        entity.setCpf(domain.getCpf());
        entity.setRg(domain.getRg());
        entity.setMatricula(domain.getMatricula());
        entity.setPosto(domain.getPosto());
        entity.setDataNascimento(domain.getDataNascimento());
        entity.setTipoSanguineo(domain.getTipoSanguineo());
        entity.setFotoUrl(domain.getFotoUrl());
        entity.setSenha(domain.getSenha());
        entity.setPerfil(domain.getPerfil());
        if (domain.getEstadoOperacional() != null) {
            entity.setEstadoOperacional(domain.getEstadoOperacional());
        }
        entity.setCentroComando(CentroComandoMapper.toEntity(domain.getCentroComando()));
        return entity;
    }
}
