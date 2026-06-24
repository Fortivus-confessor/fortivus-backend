package br.arthconf.fortivus.infrastructure.persistence.entity;

import br.arthconf.fortivus.domain.model.CategoriaOperacao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "despacho")
@Data
@EqualsAndHashCode(exclude = {"ordemServico"})
@ToString(exclude = {"ordemServico"})
public class DespachoEntity {

    @Id
    @Column(name = "id")
    private Long id; // SmartId próprio para cada despacho

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private br.arthconf.fortivus.domain.OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id")
    private br.arthconf.fortivus.domain.Escala escala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private br.arthconf.fortivus.infrastructure.persistence.entity.UsuarioEntity responsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaOperacao categoria;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "descricao_tarefa", columnDefinition = "TEXT")
    private String descricaoTarefa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private br.arthconf.fortivus.domain.SituacaoDespacho status = br.arthconf.fortivus.domain.SituacaoDespacho.EM_ANDAMENTO;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio = LocalDateTime.now();

    @Column(name = "data_fim")
    private LocalDateTime dataFim;
}
