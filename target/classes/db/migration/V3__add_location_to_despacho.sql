-- Adiciona suporte a geolocalização individual por despacho
ALTER TABLE despacho ADD COLUMN localizacao_geom geometry(Point, 4326);

-- Index espacial para performance em buscas geográficas
CREATE INDEX idx_despacho_localizacao_geom ON despacho USING GIST (localizacao_geom);
