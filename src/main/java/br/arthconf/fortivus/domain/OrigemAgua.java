package br.arthconf.fortivus.domain;

public enum OrigemAgua {
    NATURAL("Curso d'água natural"),
    HIDRANTE("Hidrante"),
    RESERVATORIO_FIXO("Reservatório fixo"),
    OUTRO("Outro");

    private final String descricao;

    OrigemAgua(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
