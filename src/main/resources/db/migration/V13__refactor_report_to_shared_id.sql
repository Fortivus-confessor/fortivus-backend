-- Refatoração do Relatório Terrestre para compartilhamento de ID com Despacho
-- 1. Remove tabelas dependentes
DROP TABLE IF EXISTS relatorio_terrestre_origens_agua;
DROP TABLE IF EXISTS relatorio_terrestre_acoes;
DROP TABLE IF EXISTS relatorio_terrestre_orgaos;
DROP TABLE IF EXISTS relatorio_terrestre_reforcos;
DROP TABLE IF EXISTS relatorio_propriedades;
DROP TABLE IF EXISTS relatorio_anexos;
DROP TABLE IF EXISTS relatorio_terrestre;

-- 2. Recria Relatório Terrestre com ID BIGINT (SmartId compartilhado)
CREATE TABLE relatorio_terrestre (
    id BIGINT PRIMARY KEY REFERENCES despacho(id),
    outros_orgaos_descricao VARCHAR(255),
    area_atuacao_geom GEOMETRY(Point, 4326),
    houve_uso_agua BOOLEAN DEFAULT FALSE,
    volume_agua_litros INTEGER,
    outra_origem_agua_descricao VARCHAR(255),
    houve_apoio_propriedades BOOLEAN DEFAULT FALSE,
    houve_recusa_propriedades BOOLEAN DEFAULT FALSE,
    possivel_origem_incendio VARCHAR(50),
    efetividade_combate VARCHAR(20),
    necessidade_reforco BOOLEAN DEFAULT FALSE,
    historico_descritivo TEXT,
    km_final DOUBLE PRECISION,
    resultado_ocorrencia VARCHAR(50),
    outro_resultado_descricao VARCHAR(255),
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP
);

-- 3. Tabelas de Coleção
CREATE TABLE relatorio_terrestre_acoes (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_terrestre(id),
    acoes_realizadas VARCHAR(50)
);

CREATE TABLE relatorio_terrestre_orgaos (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_terrestre(id),
    orgaos_apoio VARCHAR(50)
);

CREATE TABLE relatorio_terrestre_origens_agua (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_terrestre(id),
    origens_agua VARCHAR(50)
);

CREATE TABLE relatorio_terrestre_reforcos (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_terrestre(id),
    tipos_reforco_necessarios VARCHAR(50)
);

-- 4. Propriedades e Anexos (FK atualizada para BIGINT)
CREATE TABLE relatorio_propriedades (
    id UUID PRIMARY KEY,
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_terrestre(id),
    nome_propriedade VARCHAR(255),
    responsavel VARCHAR(255),
    telefone VARCHAR(50),
    localizacao_geom GEOMETRY(Point, 4326),
    tipo_registro VARCHAR(20),
    tipo_apoio VARCHAR(50),
    quantidade_apoio INTEGER,
    descricao_apoio_outro VARCHAR(255),
    motivo_recusa VARCHAR(50),
    descricao_recusa_outro VARCHAR(255)
);

CREATE TABLE relatorio_anexos (
    id UUID PRIMARY KEY,
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_terrestre(id),
    nome_arquivo VARCHAR(255),
    chave_s3 VARCHAR(255),
    content_type VARCHAR(100),
    tamanho BIGINT,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

-- Índices
CREATE INDEX idx_relatorio_terrestre_geom ON relatorio_terrestre USING GIST (area_atuacao_geom);
CREATE INDEX idx_relatorio_propriedades_geom ON relatorio_propriedades USING GIST (localizacao_geom);
