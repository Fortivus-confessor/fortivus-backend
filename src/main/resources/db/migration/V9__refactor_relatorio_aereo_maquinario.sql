-- Recreate relatorio_aereo with hexagonal refactoring columns
DROP TABLE IF EXISTS relatorio_aereo CASCADE;

CREATE TABLE relatorio_aereo (
    id                         BIGINT PRIMARY KEY REFERENCES despacho(id),
    horimetro_inicial          DOUBLE PRECISION,
    horimetro_final            DOUBLE PRECISION,
    horas_liquidas             VARCHAR(50),
    area_atuacao_geom          geometry(Point, 4326),
    qtde_lancamentos           INTEGER,
    houve_uso_agua             BOOLEAN,
    volume_agua_litros         INTEGER,
    outra_origem_agua_descricao VARCHAR(500),
    efetividade_combate        VARCHAR(50),
    necessidade_reforco        BOOLEAN,
    historico_descritivo       TEXT,
    resultado_ocorrencia       VARCHAR(100),
    outro_resultado_descricao  VARCHAR(500),
    data_inicio                TIMESTAMP NOT NULL,
    data_fim                   TIMESTAMP
);

CREATE TABLE relatorio_aereo_emprego (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_aereo(id) ON DELETE CASCADE,
    tipo_emprego VARCHAR(100) NOT NULL
);

CREATE TABLE relatorio_aereo_origem_agua (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_aereo(id) ON DELETE CASCADE,
    origem_agua  VARCHAR(100) NOT NULL
);

CREATE TABLE relatorio_aereo_reforco (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_aereo(id) ON DELETE CASCADE,
    tipo_reforco VARCHAR(100) NOT NULL
);

-- Recreate relatorio_maquinario with hexagonal refactoring columns
DROP TABLE IF EXISTS relatorio_maquinario CASCADE;

CREATE TABLE relatorio_maquinario (
    id                        BIGINT PRIMARY KEY REFERENCES despacho(id),
    horimetro_inicial         DOUBLE PRECISION,
    horimetro_final           DOUBLE PRECISION,
    tempo_liquido             VARCHAR(50),
    hora_inicio_operacao      TIME,
    hora_fim_operacao         TIME,
    comprimento_aceiros       DOUBLE PRECISION,
    descricao_outro_emprego   VARCHAR(500),
    area_atuacao_geom         geometry(Point, 4326),
    efetividade_combate       VARCHAR(50),
    necessidade_reforco       BOOLEAN,
    historico_descritivo      TEXT,
    resultado_ocorrencia      VARCHAR(100),
    outro_resultado_descricao VARCHAR(500),
    data_inicio               TIMESTAMP NOT NULL,
    data_fim                  TIMESTAMP
);

CREATE TABLE relatorio_maq_emprego (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_maquinario(id) ON DELETE CASCADE,
    tipo_emprego VARCHAR(100) NOT NULL
);

CREATE TABLE relatorio_maq_reforco (
    relatorio_id BIGINT NOT NULL REFERENCES relatorio_maquinario(id) ON DELETE CASCADE,
    tipo_reforco VARCHAR(100) NOT NULL
);
