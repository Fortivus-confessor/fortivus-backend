package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico")
@Data
@EqualsAndHashCode(exclude = {"escala", "relator"})
@ToString(exclude = {"escala", "relator"})
public class OrdemServico {

    @Id
    private Long id; // SmartId (BigInt cronológico)

    @Column(name = "localizacao_texto", columnDefinition = "TEXT")
    private String localizacaoTexto;

    @Column(name = "localizacao_geom", columnDefinition = "geometry(Geometry, 4326)")
    private Geometry localizacaoGeom;

    @Column(name = "descricao_tarefa", columnDefinition = "TEXT")
    private String descricaoTarefa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "escala_id", nullable = false)
    private Escala escala;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relator_id", nullable = false)
    private Usuario relator;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoOrdemServico status = SituacaoOrdemServico.ABERTA;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Despacho> despachos = new java.util.ArrayList<>();
}
