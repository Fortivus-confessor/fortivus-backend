ALTER TABLE ordem_servico DROP COLUMN localizacao_texto;
ALTER TABLE ordem_servico DROP COLUMN localizacao_geom;

ALTER TABLE despacho DROP COLUMN localizacao_geom;
ALTER TABLE despacho ADD COLUMN latitude DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN longitude DOUBLE PRECISION;
