-- V2: Ajuste de tipo para quilometragem de veículos
-- Altera de DOUBLE PRECISION para INTEGER conforme requisitos de negócio

ALTER TABLE public.veiculo 
ALTER COLUMN km_atual TYPE INTEGER 
USING km_atual::INTEGER;
