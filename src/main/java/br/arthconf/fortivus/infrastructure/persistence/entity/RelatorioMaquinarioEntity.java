package br.arthconf.fortivus.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "relatorio_maquinario")
@Data
@EqualsAndHashCode(exclude = {"despacho"})
@ToString(exclude = {"despacho"})
public class RelatorioMaquinarioEntity implements Persistable<Long> {

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

    @Column(name = "tempo_liquido")
    private String tempoLiquido;

    @Column(name = "hora_inicio_operacao")
    private LocalTime horaInicioOperacao;

    @Column(name = "hora_fim_operacao")
    private LocalTime horaFimOperacao;

    @ElementCollection
    @CollectionTable(name = "relatorio_maq_emprego", joinColumns = @JoinColumn(name = "relatorio_id"))
    @Column(name = "tipo_emprego")
    private List<String> tiposEmprego;

    @Column(name = "comprimento_aceiros")
    private Double comprimentoAceiros;

    @Column(name = "descricao_outro_emprego")
    private String descricaoOutroEmprego;

    @Column(name = "area_atuacao_geom", columnDefinition = "geometry(Point,4326)")
    private Point areaAtuacaoGeom;

    @Column(name = "efetividade_combate")
    private String efetividadeCombate;

    @Column(name = "necessidade_reforco")
    private Boolean necessidadeReforco;

    @ElementCollection
    @CollectionTable(name = "relatorio_maq_reforco", joinColumns = @JoinColumn(name = "relatorio_id"))
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
