package br.arthconf.fortivus.infrastructure.persistence.entity;

import br.arthconf.fortivus.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "escala")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"equipe", "veiculo", "comandante", "integrantes", "checkouts"})
@ToString(exclude = {"equipe", "veiculo", "comandante", "integrantes", "checkouts"})
public class EscalaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipe_id", nullable = false)
    private EquipeEntity equipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id")
    private VeiculoEntity veiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comandante_id", nullable = false)
    private UsuarioEntity comandante;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    private boolean ativa = true;

    @ManyToMany
    @JoinTable(
            name = "escala_usuarios",
            joinColumns = @JoinColumn(name = "escala_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<UsuarioEntity> integrantes = new ArrayList<>();

    @OneToMany(mappedBy = "escala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckoutEquipamentoEntity> checkouts = new ArrayList<>();
}
