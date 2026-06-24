package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Persistable;
import java.time.LocalDateTime;

@Entity
@Table(name = "relatorio_aereo")
@Data
@EqualsAndHashCode(exclude = {"despacho"})
@ToString(exclude = {"despacho"})
public class RelatorioAereo implements Persistable<Long> {

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

    @Column(name = "aeronave_prefixo")
    private String aeronavePrefixo;

    @Column(name = "piloto_comandante")
    private String pilotoComandante;

    @Column(name = "tempo_voo_horas")
    private Double tempoVooHoras;

    @Column(name = "volume_agua_lancado")
    private Integer volumeAguaLancado;

    @Column(name = "qtde_lancamentos")
    private Integer qtdeLancamentos;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atuacao")
    private TipoAtuacaoAerea tipoAtuacao;

    @Column(name = "historico_descritivo", columnDefinition = "TEXT")
    private String historicoDescritivo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    public enum TipoAtuacaoAerea {
        LANCAMENTO_AGUA("Lançamento de água"),
        MONITORAMENTO("Monitoramento e Patrulha"),
        TRANSPORTE("Transporte de Tropa/Equipamento");

        private final String descricao;
        TipoAtuacaoAerea(String d) { this.descricao = d; }
        public String getDescricao() { return descricao; }
    }
}
