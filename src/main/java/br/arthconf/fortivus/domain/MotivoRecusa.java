package br.arthconf.fortivus.domain;

public enum MotivoRecusa {
    PASSAGEM("Não autorizou passagem pela propriedade"),
    AGUA("Recusou fornecimento de água"),
    RECURSOS_NAO_DISPONIBILIZADOS("Não disponibilizou recursos disponíveis na propriedade"),
    COMBATE("Se recusou a combater o incêndio florestal"),
    RECOMENDACOES("Se recusou a atender as recomendações da guarnição do CBMMT (ex: aceiros)"),
    CONTRAFOGO_DESORDENADO("Realizou fogo contrafogo de maneira desordenada com o CBMMT"),
    OUTRO("Outro");

    private final String descricao;

    MotivoRecusa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
