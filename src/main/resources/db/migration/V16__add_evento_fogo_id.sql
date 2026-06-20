ALTER TABLE ordem_servico DROP CONSTRAINT IF EXISTS fk_ordem_servico_foco_incendio;
ALTER TABLE ordem_servico DROP COLUMN IF EXISTS foco_incendio_id;
ALTER TABLE ordem_servico ADD COLUMN evento_fogo_id UUID UNIQUE;
