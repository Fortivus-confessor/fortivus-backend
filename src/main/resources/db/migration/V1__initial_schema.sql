-- V1: Schema Inicial Consolidado FORTIVUS V2
-- CriaÃ§Ã£o dos schemas
CREATE SCHEMA IF NOT EXISTS operacional;
CREATE SCHEMA IF NOT EXISTS fire_events;

-- O Flyway rodarÃ¡ com default_schema = operacional
-- A extensÃ£o PostGIS precisa estar no public ou no schema atual
CREATE EXTENSION IF NOT EXISTS "postgis" SCHEMA public;

-- 1. GestÃ£o Organizacional (Centros e Equipes)
CREATE TABLE centro_comando (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    endereco TEXT,
    telefone VARCHAR(50),
    central BOOLEAN DEFAULT FALSE,
    geom GEOMETRY(Point, 4326)
);

CREATE TABLE equipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(50) NOT NULL, -- TERRESTRE, AEREO, MAQUINARIO
    centro_comando_id UUID NOT NULL REFERENCES centro_comando(id)
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    primeiro_nome VARCHAR(100),
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    rg VARCHAR(50) UNIQUE,
    matricula VARCHAR(50) UNIQUE,
    posto VARCHAR(100),
    data_nascimento DATE,
    tipo_sanguineo VARCHAR(10),
    foto_url TEXT,
    perfil VARCHAR(50) NOT NULL, -- ROLE_ADMIN, ROLE_CENTRO_COMANDO_CENTRAL, ROLE_CENTRO_COMANDO
    estado_operacional VARCHAR(50) DEFAULT 'DISPONIVEL',
    centro_comando_id UUID REFERENCES centro_comando(id),
    equipe_id UUID REFERENCES equipes(id)
);

-- 2. GestÃ£o de Ativos (LogÃ­stica & Frota)
CREATE TABLE veiculo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    identificador VARCHAR(100) UNIQUE NOT NULL, -- Placa ou Prefixo
    prefixo VARCHAR(50),
    modelo VARCHAR(255) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    km_atual INTEGER DEFAULT 0,
    foto_url TEXT,
    equipe_id UUID REFERENCES equipes(id)
);

CREATE TABLE equipamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    identificador VARCHAR(100) UNIQUE NOT NULL, -- PatrimÃ´nio
    estado VARCHAR(50) DEFAULT 'OPERANTE',
    equipe_id UUID REFERENCES equipes(id)
);

-- 3. Operacional (Combate)
CREATE TABLE escala (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipe_id UUID NOT NULL REFERENCES equipes(id),
    veiculo_id UUID REFERENCES veiculo(id),
    comandante_id UUID NOT NULL REFERENCES usuarios(id),
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP,
    ativa BOOLEAN DEFAULT TRUE
);

CREATE TABLE escala_usuarios (
    escala_id UUID NOT NULL REFERENCES escala(id),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    PRIMARY KEY (escala_id, usuario_id)
);

CREATE TABLE checkout_equipamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    escala_id UUID NOT NULL REFERENCES escala(id),
    equipamento_id UUID NOT NULL REFERENCES equipamento(id),
    responsavel_entrega_id UUID NOT NULL REFERENCES usuarios(id),
    data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_devolucao TIMESTAMP,
    responsavel_recebimento_id UUID REFERENCES usuarios(id)
);

CREATE TABLE ordem_servico (
    id BIGINT PRIMARY KEY, -- SmartId
    localizacao_texto TEXT,
    localizacao_geom GEOMETRY(Geometry, 4326),
    descricao_tarefa TEXT,
    escala_id UUID NOT NULL REFERENCES escala(id),
    relator_id UUID NOT NULL REFERENCES usuarios(id),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_fim TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ABERTA',
    evento_fogo_id BIGINT UNIQUE
);

CREATE TABLE despacho (
    id BIGINT PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordem_servico(id),
    status VARCHAR(50) DEFAULT 'EM_ANDAMENTO',
    data_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_fim TIMESTAMP,
    localizacao_geom GEOMETRY(Point, 4326),
    escala_id UUID REFERENCES escala(id),
    descricao_tarefa TEXT,
    resumo_final TEXT,
    categoria VARCHAR(50)
);

CREATE INDEX idx_despacho_localizacao_geom ON despacho USING GIST (localizacao_geom);

-- 4. RelatÃ³rios
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

CREATE INDEX idx_relatorio_terrestre_geom ON relatorio_terrestre USING GIST (area_atuacao_geom);

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

CREATE INDEX idx_relatorio_propriedades_geom ON relatorio_propriedades USING GIST (localizacao_geom);

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

-- 5. Auditoria (Envers)
CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE revinfo (
    rev INTEGER PRIMARY KEY,
    revtstmp BIGINT,
    usuario_id UUID
);

CREATE TABLE usuarios_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    nome VARCHAR(255),
    primeiro_nome VARCHAR(100),
    email VARCHAR(255),
    cpf VARCHAR(14),
    rg VARCHAR(50),
    matricula VARCHAR(50),
    posto VARCHAR(100),
    data_nascimento DATE,
    tipo_sanguineo VARCHAR(10),
    foto_url TEXT,
    perfil VARCHAR(50),
    estado_operacional VARCHAR(50),
    centro_comando_id UUID,
    equipe_id UUID,
    PRIMARY KEY (id, rev)
);

-- 6. Bootstrap Data
INSERT INTO centro_comando (id, nome, endereco, central) 
VALUES ('00000000-0000-0000-0000-000000000001', 'SEDE CENTRAL FORTIVUS', 'AVENIDA DO COMANDO, 1000 - CUIABÃ', TRUE);

INSERT INTO usuarios (id, nome, email, perfil, centro_comando_id, estado_operacional)
VALUES (gen_random_uuid(), 'Admin Fortivus', 'admin@fortivus.local', 'ROLE_ADMIN', '00000000-0000-0000-0000-000000000001', 'DISPONIVEL');

INSERT INTO usuarios (id, nome, email, perfil, centro_comando_id, estado_operacional)
VALUES (gen_random_uuid(), 'akadmin', 'akadmin@localhost', 'ROLE_ADMIN', '00000000-0000-0000-0000-000000000001', 'DISPONIVEL');
