package br.arthconf.fortivus.infrastructure.persistence.entity;

import br.arthconf.fortivus.domain.model.CategoriaOperacao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import br.arthconf.fortivus.domain.BaseEntity;

@Entity
@Table(name = "veiculo")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"equipe"})
@ToString(exclude = {"equipe"})
public class VeiculoEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String identificador;

    private String prefixo;

    @Column(nullable = false)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaOperacao categoria;

    @Column(name = "km_atual")
    private Integer kmAtual = 0;

    @Column(length = 50)
    private String contrato;

    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private br.arthconf.fortivus.infrastructure.persistence.entity.EquipeEntity equipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_comando_id")
    private CentroComandoEntity centroComando;
}
