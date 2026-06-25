package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.CentroComando;
import br.arthconf.fortivus.infrastructure.persistence.entity.CentroComandoEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class CentroComandoMapper {

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    public static CentroComando toDomain(CentroComandoEntity entity) {
        if (entity == null) return null;
        Double lat = null, lng = null;
        if (entity.getGeom() != null) {
            lat = entity.getGeom().getY();
            lng = entity.getGeom().getX();
        }
        return CentroComando.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .endereco(entity.getEndereco())
                .telefone(entity.getTelefone())
                .central(entity.isCentral())
                .latitude(lat)
                .longitude(lng)
                .build();
    }

    public static CentroComandoEntity toEntity(CentroComando domain) {
        if (domain == null) return null;
        CentroComandoEntity entity = new CentroComandoEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setEndereco(domain.getEndereco());
        entity.setTelefone(domain.getTelefone());
        entity.setCentral(domain.isCentral());
        if (domain.getLatitude() != null && domain.getLongitude() != null) {
            entity.setGeom(GF.createPoint(new Coordinate(domain.getLongitude(), domain.getLatitude())));
        }
        return entity;
    }
}
