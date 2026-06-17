package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "equipamento")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"equipe"})
@ToString(exclude = {"equipe"})
public class Equipamento extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String identificador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEquipamento estado = EstadoEquipamento.OPERANTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
}
