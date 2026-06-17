package br.arthconf.fortivus.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "centro_comando")
@Data
@EqualsAndHashCode(callSuper = true)
public class CentroComando extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    private String endereco;

    private String telefone;

    @Column(name = "central")
    private boolean central = false;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private org.locationtech.jts.geom.Point geom;

    @jakarta.persistence.Transient
    private Double latitude;

    @jakarta.persistence.Transient
    private Double longitude;

    public void updateGeom() {
        if (latitude != null && longitude != null) {
            org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
            this.geom = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
        }
    }

    public void loadCoordinates() {
        if (this.geom != null) {
            this.latitude = this.geom.getY();
            this.longitude = this.geom.getX();
        }
    }
}
