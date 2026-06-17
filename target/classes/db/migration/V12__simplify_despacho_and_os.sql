-- Simplificação da tabela Despacho e ajuste na Ordem de Serviço
-- 1. Transfere a categoria da OS para o Despacho antes de remover da OS
ALTER TABLE despacho ADD COLUMN categoria VARCHAR(50);

UPDATE despacho d 
SET categoria = (SELECT categoria FROM ordem_servico os WHERE os.id = d.ordem_servico_id);

-- 2. Remove a categoria da Ordem de Serviço
ALTER TABLE ordem_servico DROP COLUMN categoria;

-- 3. Remove os campos técnicos redundantes do Despacho (agora pertencem aos Relatórios)
ALTER TABLE despacho DROP COLUMN km_inicial;
ALTER TABLE despacho DROP COLUMN km_final;
ALTER TABLE despacho DROP COLUMN combustivel_consumido;
ALTER TABLE despacho DROP COLUMN area_combatida_hectares;
ALTER TABLE despacho DROP COLUMN hobbs_inicial;
ALTER TABLE despacho DROP COLUMN hobbs_final;
ALTER TABLE despacho DROP COLUMN quantidade_lancamentos;
ALTER TABLE despacho DROP COLUMN volume_agua_litros;
ALTER TABLE despacho DROP COLUMN horimetro_inicial;
ALTER TABLE despacho DROP COLUMN horimetro_final;
ALTER TABLE despacho DROP COLUMN km_aceiro_realizado;
ALTER TABLE despacho DROP COLUMN implemento_utilizado;
ALTER TABLE despacho DROP COLUMN resumo_final;
