package br.arthconf.fortivus.infrastructure.persistence.entity;

import br.arthconf.fortivus.domain.model.CategoriaOperacao;
import br.arthconf.fortivus.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "equipes")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"centroComando", "usuarios"})
@ToString(exclude = {"centroComando", "usuarios"})
public class EquipeEntity extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaOperacao categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "centro_comando_id", nullable = false)
    private CentroComandoEntity centroComando;

    @OneToMany(mappedBy = "equipe")
    private List<UsuarioEntity> usuarios;
}
