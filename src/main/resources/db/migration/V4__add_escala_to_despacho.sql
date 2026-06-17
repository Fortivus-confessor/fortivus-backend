-- Adiciona vínculo de escala ao despacho
ALTER TABLE despacho ADD COLUMN escala_id uuid;

-- Adiciona constraint de chave estrangeira
ALTER TABLE despacho ADD CONSTRAINT fk_despacho_escala FOREIGN KEY (escala_id) REFERENCES escala(id);
