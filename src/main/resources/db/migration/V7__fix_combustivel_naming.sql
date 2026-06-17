-- Corrige o nome da coluna de combustível (remove acento) se ela foi criada incorretamente pela V6
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='despacho' AND column_name='combustível_consumido') THEN
        ALTER TABLE despacho RENAME COLUMN combustível_consumido TO combustivel_consumido;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='despacho' AND column_name='combustivel_consumido') THEN
        ALTER TABLE despacho ADD COLUMN combustivel_consumido DOUBLE PRECISION;
    END IF;
END $$;
