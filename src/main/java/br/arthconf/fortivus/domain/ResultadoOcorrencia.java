package br.arthconf.fortivus.domain;

public enum ResultadoOcorrencia {
    EM_ANDAMENTO("Em andamento"),
    NECESSIDADE_FISCALIZACAO("Necessidade de emprego de equipe de fiscalização"),
    SEM_INTERVENCAO("Sem necessidade de intervenção do CBMMT"),
    EXTINTO_RESOLVIDA("Incêndio extinto pelo CBMMT / Resolvida"),
    DESPACHO_INCORRETO("Despacho incorreto"),
    OUTRO("Outro");

    private final String descricao;
    ResultadoOcorrencia(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
