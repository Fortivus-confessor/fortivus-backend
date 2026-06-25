package br.arthconf.fortivus.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "relatorio_aereo")
@Data
@EqualsAndHashCode(exclude = {"despacho"})
@ToString(exclude = {"despacho"})
public class RelatorioAereoEntity implements Persistable<Long> {

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
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private DespachoEntity despacho;

    @Column(name = "horimetro_inicial")
    private Double horimetroInicial;

    @Column(name = "horimetro_final")
    private Double horimetroFinal;

    @Column(name = "horas_liquidas")
    private String horasLiquidas;

    @ElementCollection
    @CollectionTable(name = "relatorio_aereo_emprego", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Column(name = "tipo_emprego")
    private List<String> tiposEmprego;

    @Column(name = "area_atuacao_geom", columnDefinition = "geometry(Point,4326)")
    private Point areaAtuacaoGeom;

    @Column(name = "qtde_lancamentos")
    private Integer qtdeLancamentos;

    @Column(name = "houve_uso_agua")
    private Boolean houveUsoAgua;

    @Column(name = "volume_agua_litros")
    private Integer volumeAguaLitros;

    @ElementCollection
    @CollectionTable(name = "relatorio_aereo_origem_agua", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Column(name = "origem_agua")
    private List<String> origensAgua;

    @Column(name = "outra_origem_agua_descricao")
    private String outraOrigemAguaDescricao;

    @Column(name = "efetividade_combate")
    private String efetividadeCombate;

    @Column(name = "necessidade_reforco")
    private Boolean necessidadeReforco;

    @ElementCollection
    @CollectionTable(name = "relatorio_aereo_reforco", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Column(name = "tipo_reforco")
    private List<String> tiposReforcoNecessarios;

    @Column(name = "historico_descritivo", columnDefinition = "TEXT")
    private String historicoDescritivo;

    @Column(name = "resultado_ocorrencia")
    private String resultadoOcorrencia;

    @Column(name = "outro_resultado_descricao")
    private String outroResultadoDescricao;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;
}
