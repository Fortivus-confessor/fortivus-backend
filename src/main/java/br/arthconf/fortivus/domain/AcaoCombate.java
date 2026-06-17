package br.arthconf.fortivus.domain;

public enum AcaoCombate {
    RECONHECIMENTO_PLANEJAMENTO("Reconhecimento e Planejamento"),
    COMBATE_DIRETO("Combate a Incêndio florestal direto"),
    ACEIRO_MANUAL("Confecção de aceiro manual"),
    ACEIRO_MECANICO_APOIO("Confecção de aceiro mecânico com apoio de terceiros"),
    FOGO_CONTRAFOGO("Realização de fogo contrafogo"),
    VIGILANCIA("Vigilância"),
    RESCALDO("Rescaldo"),
    NENHUMA("Nenhuma ação foi executada");

    private final String descricao;
    AcaoCombate(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
