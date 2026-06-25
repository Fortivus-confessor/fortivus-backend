package br.arthconf.fortivus.domain;

public enum TipoApoio {
    MAQUINARIO("Maquinário (tratores, pás, caminhão-pipa)"),
    MAO_DE_OBRA("Mão de obra (funcionários, brigadistas)"),
    OUTRO("Outro");

    private final String descricao;

    TipoApoio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
