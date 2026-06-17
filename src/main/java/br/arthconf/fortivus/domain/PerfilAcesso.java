package br.arthconf.fortivus.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerfilAcesso {
    ROLE_ADMIN("Administrador", "Acesso total e gestão do ecossistema."),
    ROLE_CENTRO_COMANDO_CENTRAL("Centro de Comando Central", "Gestão estratégica e supervisão geral."),
    ROLE_CENTRO_COMANDO("Centro de Comando", "Gestão tática local e operacional."),
    ROLE_COMBATENTE("Combatente", "Operacional de campo e execução de missões.");

    private final String descricao;
    private final String detalhe;

    public String getRole() {
        return this.name();
    }
}
