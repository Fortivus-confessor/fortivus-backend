package br.arthconf.fortivus.domain;

import lombok.Getter;

@Getter
public enum EstadoOperacionalUsuario {
    DISPONIVEL("Disponível"),
    FERIAS("Férias"),
    AFASTADO_SAUDE("Afastado por Saúde"),
    LICENCA("Licença"),
    EM_MISSAO("Em Missão");

    private final String descricao;

    EstadoOperacionalUsuario(String descricao) {
        this.descricao = descricao;
    }
}
