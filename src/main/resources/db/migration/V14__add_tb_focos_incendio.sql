CREATE TABLE tb_focos_incendio (
    id UUID PRIMARY KEY,
    codigo_inpe VARCHAR(255) UNIQUE,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    bioma VARCHAR(255),
    municipio VARCHAR(255),
    estado VARCHAR(255),
    satelite_referencia VARCHAR(255),
    risco_fogo VARCHAR(255),
    frp DOUBLE PRECISION,
    area_estimada_hectares DOUBLE PRECISION,
    data_hora_deteccao TIMESTAMP NOT NULL,
    origem_registro VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    cadastrado_por_id VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
