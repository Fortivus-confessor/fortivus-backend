ALTER TABLE despacho ADD COLUMN responsavel_id UUID;
ALTER TABLE despacho ADD CONSTRAINT fk_despacho_responsavel FOREIGN KEY (responsavel_id) REFERENCES usuarios(id);
