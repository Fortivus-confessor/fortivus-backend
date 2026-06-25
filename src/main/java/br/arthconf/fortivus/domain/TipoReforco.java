package br.arthconf.fortivus.domain;

public enum TipoReforco {
    TERRESTRE("Mais guarnições terrestres"),
    AEREO("Apoio aéreo"),
    MAQUINARIO("Maquinário pesado"),
    SCI("Implantação do SCI");

    private final String descricao;

    TipoReforco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
