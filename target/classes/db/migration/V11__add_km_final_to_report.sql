-- Adiciona a coluna km_final à tabela relatorio_terrestre para persistência tática
ALTER TABLE relatorio_terrestre ADD COLUMN IF NOT EXISTS km_final DOUBLE PRECISION;
