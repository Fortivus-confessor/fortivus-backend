package br.arthconf.fortivus.domain;

import lombok.Getter;

@Getter
public enum SituacaoDespacho {
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído"),
    PENDENTE_RELATORIO("Pendente Relatório");

    private final String descricao;

    SituacaoDespacho(String descricao) {
        this.descricao = descricao;
    }
}
