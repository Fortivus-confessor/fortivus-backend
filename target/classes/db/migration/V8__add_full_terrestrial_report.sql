-- Estrutura para Relatório Terrestre Completo
CREATE TABLE relatorio_terrestre (
    id UUID PRIMARY KEY,
    despacho_id BIGINT NOT NULL REFERENCES despacho(id),
    outros_orgaos_descricao VARCHAR(255),
    area_atuacao_geom GEOMETRY(Point, 4326),
    houve_uso_agua BOOLEAN DEFAULT FALSE,
    volume_agua_litros INTEGER,
    origem_agua VARCHAR(50),
    outra_origem_agua_descricao VARCHAR(255),
    houve_apoio_propriedades BOOLEAN DEFAULT FALSE,
    houve_recusa_propriedades BOOLEAN DEFAULT FALSE,
    possivel_origem_incendio VARCHAR(50),
    efetividade_combate VARCHAR(20),
    necessidade_reforco BOOLEAN DEFAULT FALSE,
    historico_descritivo TEXT,
    resultado_ocorrencia VARCHAR(50),
    outro_resultado_descricao VARCHAR(255)
);

-- Tabelas de Coleção (ElementCollection)
CREATE TABLE relatorio_terrestre_acoes (
    relatorio_id UUID NOT NULL REFERENCES relatorio_terrestre(id),
    acoes_realizadas VARCHAR(50)
);

CREATE TABLE relatorio_terrestre_orgaos (
    relatorio_id UUID NOT NULL REFERENCES relatorio_terrestre(id),
    orgaos_apoio VARCHAR(50)
);

CREATE TABLE relatorio_terrestre_reforcos (
    relatorio_id UUID NOT NULL REFERENCES relatorio_terrestre(id),
    tipos_reforco_necessarios VARCHAR(50)
);

-- Tabela de Propriedades Rurais (Apoio/Recusa)
CREATE TABLE relatorio_propriedades (
    id UUID PRIMARY KEY,
    relatorio_id UUID NOT NULL REFERENCES relatorio_terrestre(id),
    nome_propriedade VARCHAR(255),
    responsavel VARCHAR(255),
    telefone VARCHAR(20),
    localizacao_geom GEOMETRY(Point, 4326),
    tipo_registro VARCHAR(10), -- APOIO, RECUSA
    tipo_apoio VARCHAR(50),
    quantidade_apoio INTEGER,
    descricao_apoio_outro VARCHAR(255),
    motivo_recusa VARCHAR(50),
    descricao_recusa_outro VARCHAR(255)
);

-- Índices Espaciais
CREATE INDEX idx_relatorio_terrestre_geom ON relatorio_terrestre USING GIST (area_atuacao_geom);
CREATE INDEX idx_relatorio_propriedades_geom ON relatorio_propriedades USING GIST (localizacao_geom);
