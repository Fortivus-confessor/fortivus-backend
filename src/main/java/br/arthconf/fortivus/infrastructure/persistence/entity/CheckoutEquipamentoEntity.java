package br.arthconf.fortivus.infrastructure.persistence.entity;

import br.arthconf.fortivus.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkout_equipamento")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"escala", "equipamento", "responsavelEntrega", "responsavelRecebimento"})
@ToString(exclude = {"escala", "equipamento", "responsavelEntrega", "responsavelRecebimento"})
public class CheckoutEquipamentoEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "escala_id", nullable = false)
    private EscalaEntity escala;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private EquipamentoEntity equipamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsavel_entrega_id", nullable = false)
    private UsuarioEntity responsavelEntrega;

    @Column(name = "data_emprestimo", nullable = false)
    private LocalDateTime dataEmprestimo = LocalDateTime.now();

    @Column(name = "data_devolucao")
    private LocalDateTime dataDevolucao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_recebimento_id")
    private UsuarioEntity responsavelRecebimento;
}
