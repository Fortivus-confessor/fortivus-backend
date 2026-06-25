package br.arthconf.fortivus.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;
import br.arthconf.fortivus.domain.SituacaoOrdemServico;

@Entity
@Table(name = "ordem_servico")
@Data
@EqualsAndHashCode(exclude = {"escala", "relator"})
@ToString(exclude = {"escala", "relator"})
public class OrdemServicoEntity {

    @Id
    private Long id;    

    @Column(name = "descricao_tarefa", columnDefinition = "TEXT")
    private String descricaoTarefa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "escala_id", nullable = false)
    private EscalaEntity escala;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relator_id", nullable = false)
    private br.arthconf.fortivus.infrastructure.persistence.entity.UsuarioEntity relator;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoOrdemServico status = SituacaoOrdemServico.ABERTA;

    @Column(name = "evento_fogo_id", unique = true)
    private Long eventoFogoId;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<DespachoEntity> despachos = new java.util.ArrayList<>();
}


