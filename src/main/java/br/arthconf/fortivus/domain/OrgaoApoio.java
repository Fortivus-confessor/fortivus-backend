package br.arthconf.fortivus.domain;

public enum OrgaoApoio {
    EXERCITO("Exército Brasileiro"),
    FAB("Força Aérea do Brasil"),
    MARINHA("Marinha do Brasil"),
    PM("Polícia Militar"),
    ICMBIO("ICMBio"),
    IBAMA("Ibama"),
    SINFRA("SINFRA"),
    SEMA("SEMA"),
    DEFESA_CIVIL("Defesa Civil Estadual"),
    PREFEITURA("Prefeitura Municipal"),
    OUTROS("Outros"),
    NENHUM("Nenhum órgão de Apoio");

    private final String descricao;
    OrgaoApoio(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
