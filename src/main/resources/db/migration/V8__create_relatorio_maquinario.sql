CREATE TABLE relatorio_maquinario (
    id BIGINT PRIMARY KEY REFERENCES despacho(id),
    operador VARCHAR(255),
    horas_trabalhadas DOUBLE PRECISION,
    tipo_maquinario VARCHAR(50),
    extensao_linha_defesa_metros DOUBLE PRECISION,
    historico_descritivo TEXT,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP
);
