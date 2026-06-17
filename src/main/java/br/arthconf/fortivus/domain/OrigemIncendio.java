package br.arthconf.fortivus.domain;

public enum OrigemIncendio {
    RAIO("Raio (descarga elétrica atmosférica)"),
    QUEIMADA_LIXO("Queimada ilegal de lixo e folhas"),
    QUEIMA_LENHOSO("Queima ilegal de material lenhoso enleirado"),
    ACIDENTE_VEICULAR("Acidente veicular"),
    INTENCIONAL("Ação intencional (incendiário / criminosa)"),
    EXTRATIVISMO("Atividade extrativista (carvão, mel, coleta, etc.)"),
    REDE_ELETRICA("Problemas na rede elétrica"),
    SEM_INDICIOS("Sem indícios da possível causa");

    private final String descricao;
    OrigemIncendio(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
