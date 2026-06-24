CREATE TABLE relatorio_aereo (
    id BIGINT PRIMARY KEY REFERENCES despacho(id),
    aeronave_prefixo VARCHAR(255),
    piloto_comandante VARCHAR(255),
    tempo_voo_horas DOUBLE PRECISION,
    volume_agua_lancado INTEGER,
    qtde_lancamentos INTEGER,
    tipo_atuacao VARCHAR(50),
    historico_descritivo TEXT,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP
);
