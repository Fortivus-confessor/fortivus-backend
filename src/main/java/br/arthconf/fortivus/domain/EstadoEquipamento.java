package br.arthconf.fortivus.domain;

import lombok.Getter;

@Getter
public enum EstadoEquipamento {
    OPERANTE("Operante / Utilizável"),
    MANUTENCAO("Em Manutenção"),
    ESTRAGADO("Estragado / Inoperante"),
    EXTRAVIADO("Extraviado");

    private final String descricao;

    EstadoEquipamento(String descricao) {
        this.descricao = descricao;
    }
}
