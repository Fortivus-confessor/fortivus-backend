CREATE TABLE tb_eventos_fogo (
    id UUID PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    descricao TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    data_criacao TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL
);
