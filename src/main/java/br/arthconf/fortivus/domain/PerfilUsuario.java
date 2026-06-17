package br.arthconf.fortivus.domain;

import lombok.Getter;

@Getter
public enum PerfilUsuario {
    ADMIN("ROLE_ADMIN", "Administrador"),
    CENTRO_COMANDO_CENTRAL("ROLE_CENTRO_COMANDO_CENTRAL", "Comando Central"),
    CENTRO_COMANDO("ROLE_CENTRO_COMANDO", "Comando Regional");

    private final String role;
    private final String descricao;

    PerfilUsuario(String role, String descricao) {
        this.role = role;
        this.descricao = descricao;
    }
}
