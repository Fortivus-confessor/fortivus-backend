package br.arthconf.fortivus.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.locationtech.jts.geom.Point;

import java.util.UUID;

@Entity
@Table(name = "centro_comando")
@Data
@EqualsAndHashCode(of = "id")
public class CentroComandoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String endereco;

    private String telefone;

    @Column(name = "central")
    private boolean central = false;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point geom;
}
