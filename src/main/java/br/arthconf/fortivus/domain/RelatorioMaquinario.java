package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Persistable;
import java.time.LocalDateTime;

@Entity
@Table(name = "relatorio_maquinario")
@Data
@EqualsAndHashCode(exclude = {"despacho"})
@ToString(exclude = {"despacho"})
public class RelatorioMaquinario implements Persistable<Long> {

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
    private Despacho despacho;

    @Column(name = "operador")
    private String operador;

    @Column(name = "horas_trabalhadas")
    private Double horasTrabalhadas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_maquinario")
    private TipoMaquinario tipoMaquinario;

    @Column(name = "extensao_linha_defesa_metros")
    private Double extensaoLinhaDefesaMetros;

    @Column(name = "historico_descritivo", columnDefinition = "TEXT")
    private String historicoDescritivo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    public enum TipoMaquinario {
        TRATOR_ESTEIRA("Trator de Esteira"),
        MOTONIVELADORA("Motoniveladora"),
        PA_CARREGADEIRA("Pá Carregadeira"),
        TRATOR_PNEU("Trator de Pneu"),
        OUTROS("Outros");

        private final String descricao;
        TipoMaquinario(String d) { this.descricao = d; }
        public String getDescricao() { return descricao; }
    }
}
