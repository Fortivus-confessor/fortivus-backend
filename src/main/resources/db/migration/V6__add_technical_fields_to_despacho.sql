-- Adiciona campos técnicos de fechamento ao despacho
ALTER TABLE despacho ADD COLUMN resumo_final TEXT;

-- Terrestre
ALTER TABLE despacho ADD COLUMN km_inicial DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN km_final DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN combustivel_consumido DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN area_combatida_hectares DOUBLE PRECISION;

-- Aéreo
ALTER TABLE despacho ADD COLUMN hobbs_inicial DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN hobbs_final DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN quantidade_lancamentos INTEGER;
ALTER TABLE despacho ADD COLUMN volume_agua_litros INTEGER;

-- Maquinário
ALTER TABLE despacho ADD COLUMN horimetro_inicial DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN horimetro_final DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN km_aceiro_realizado DOUBLE PRECISION;
ALTER TABLE despacho ADD COLUMN implemento_utilizado VARCHAR(255);
