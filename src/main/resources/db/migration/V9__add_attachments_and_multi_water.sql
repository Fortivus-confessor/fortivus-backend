-- Ajustes para suportar múltiplas origens de água e anexos
ALTER TABLE relatorio_terrestre DROP COLUMN origem_agua;

CREATE TABLE relatorio_terrestre_origens_agua (
    relatorio_id UUID NOT NULL REFERENCES relatorio_terrestre(id),
    origens_agua VARCHAR(50)
);

CREATE TABLE relatorio_anexos (
    id UUID PRIMARY KEY,
    relatorio_id UUID NOT NULL REFERENCES relatorio_terrestre(id),
    nome_arquivo VARCHAR(255),
    chave_s3 VARCHAR(255),
    content_type VARCHAR(100),
    tamanho BIGINT,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);
