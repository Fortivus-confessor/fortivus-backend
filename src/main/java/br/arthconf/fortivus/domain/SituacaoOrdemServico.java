package br.arthconf.fortivus.domain;

import lombok.Getter;

@Getter
public enum SituacaoOrdemServico {
    ABERTA("Aberta"),
    EM_EXECUCAO("Em Execução"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    SituacaoOrdemServico(String descricao) {
        this.descricao = descricao;
    }
}
