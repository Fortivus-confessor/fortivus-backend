package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.domain.Persistable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "relatorio_terrestre")
@Data
@EqualsAndHashCode(exclude = {"despacho"})
@ToString(exclude = {"despacho"})
public class RelatorioTerrestre implements Persistable<Long> {

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @Id
    @Column(name = "id")
    private Long id; // Compartilha o ID do Despacho (BigInt)

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Despacho despacho;

    @ElementCollection(targetClass = AcaoCombate.class)
    @CollectionTable(name = "relatorio_terrestre_acoes", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Enumerated(EnumType.STRING)
    private Set<AcaoCombate> acoesRealizadas;

    @ElementCollection(targetClass = OrgaoApoio.class)
    @CollectionTable(name = "relatorio_terrestre_orgaos", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "orgaos_apoio")
    private Set<OrgaoApoio> orgaosApoio;

    @Column(name = "outros_orgaos_descricao")
    private String outrosOrgaosDescricao;

    @Column(name = "area_atuacao_geom", columnDefinition = "geometry(Point, 4326)")
    private Geometry areaAtuacaoGeom;

    @Column(name = "houve_uso_agua")
    private Boolean houveUsoAgua;

    @Column(name = "volume_agua_litros")
    private Integer volumeAguaLitros;

    @ElementCollection(targetClass = OrigemAgua.class)
    @CollectionTable(name = "relatorio_terrestre_origens_agua", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "origens_agua")
    private Set<OrigemAgua> origensAgua;

    @Column(name = "outra_origem_agua_descricao")
    private String outraOrigemAguaDescricao;

    @Column(name = "houve_apoio_propriedades")
    private Boolean houveApoioPropriedades;

    @Column(name = "houve_recusa_propriedades")
    private Boolean houveRecusaPropriedades;

    @Enumerated(EnumType.STRING)
    @Column(name = "possivel_origem_incendio")
    private OrigemIncendio possivelOrigemIncendio;

    @Enumerated(EnumType.STRING)
    @Column(name = "efetividade_combate")
    private EfetividadeCombate efetividadeCombate;

    @Column(name = "necessidade_reforco")
    private Boolean necessidadeReforco;
    
    @ElementCollection(targetClass = TipoReforco.class)
    @CollectionTable(name = "relatorio_terrestre_reforcos", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipos_reforco_necessarios")
    private Set<TipoReforco> tiposReforcoNecessarios;

    @Column(name = "historico_descritivo", columnDefinition = "TEXT")
    private String historicoDescritivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado_ocorrencia")
    private ResultadoOcorrencia resultadoOcorrencia;

    @Column(name = "outro_resultado_descricao")
    private String outroResultadoDescricao;

    @OneToMany(mappedBy = "relatorio", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PropriedadeRelatorio> propriedades = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "relatorio", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<AnexoRelatorio> anexos = new java.util.ArrayList<>();

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    public enum OrigemAgua {
        NATURAL("Curso d’água natural"),
        HIDRANTE("Hidrante"),
        RESERVATORIO_FIXO("Reservatório fixo"),
        OUTRO("Outro");
        private final String descricao;
        OrigemAgua(String d) { this.descricao = d; }
        public String getDescricao() { return descricao; }
    }

    public enum EfetividadeCombate {
        ALTA, MEDIA, BAIXA
    }

    public enum TipoReforco {
        TERRESTRE("Mais guarnições terrestres"),
        AEREO("Apoio aéreo"),
        MAQUINARIO("Maquinário pesado"),
        SCI("Implantação do SCI");
        private final String descricao;
        TipoReforco(String d) { this.descricao = d; }
        public String getDescricao() { return descricao; }
    }
}
