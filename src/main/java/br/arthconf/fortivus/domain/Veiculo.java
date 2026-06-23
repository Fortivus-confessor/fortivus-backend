package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "veiculo")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"equipe"})
@ToString(exclude = {"equipe"})
public class Veiculo extends BaseEntity {

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

    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_comando_id")
    private CentroComando centroComando;
}
