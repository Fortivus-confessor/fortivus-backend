ALTER TABLE ordem_servico ADD COLUMN foco_incendio_id UUID;
ALTER TABLE ordem_servico ADD CONSTRAINT fk_ordem_servico_foco_incendio FOREIGN KEY (foco_incendio_id) REFERENCES tb_focos_incendio(id);
