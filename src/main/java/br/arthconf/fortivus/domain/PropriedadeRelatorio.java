package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "relatorio_propriedades")
@Data
@EqualsAndHashCode(callSuper = true)
public class PropriedadeRelatorio extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioTerrestre relatorio;

    @Column(name = "nome_propriedade")
    private String nomePropriedade;

    private String responsavel;
    private String telefone;

    @Column(name = "localizacao_geom", columnDefinition = "geometry(Point, 4326)")
    private Geometry localizacaoGeom;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro")
    private TipoRegistro tipoRegistro; // APOIO ou RECUSA

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_apoio")
    private TipoApoio tipoApoio;

    @Column(name = "quantidade_apoio")
    private Integer quantidadeApoio;

    @Column(name = "descricao_apoio_outro")
    private String descricaoApoioOutro;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_recusa")
    private MotivoRecusa motivoRecusa;

    @Column(name = "descricao_recusa_outro")
    private String descricaoRecusaOutro;

    public enum TipoRegistro {
        APOIO, RECUSA
    }

    public enum TipoApoio {
        MAQUINARIO("Maquinário (tratores, pás, caminhão-pipa)"),
        MAO_DE_OBRA("Mão de obra (funcionários, brigadistas)"),
        OUTRO("Outro");
        private final String descricao;
        TipoApoio(String d) { this.descricao = d; }
        public String getDescricao() { return descricao; }
    }

    public enum MotivoRecusa {
        PASSAGEM("Não autorizou passagem pela propriedade"),
        AGUA("Recusou fornecimento de água"),
        RECURSOS_NAO_DISPONIBILIZADOS("Não disponibilizou recursos disponíveis na propriedade"),
        COMBATE("Se recusou a combater o incêndio florestal"),
        RECOMENDACOES("Se recusou a atender as recomendações da guarnição do CBMMT (ex: aceiros)"),
        CONTRAFOGO_DESORDENADO("Realizou fogo contrafogo de maneira desordenada com o CBMMT"),
        OUTRO("Outro");
        private final String descricao;
        MotivoRecusa(String d) { this.descricao = d; }
        public String getDescricao() { return descricao; }
    }
}
