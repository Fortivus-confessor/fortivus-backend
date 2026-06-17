-- Corrige o nome da coluna de chave S3 (remove s3_key em favor de chave_s3) se necessário
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='relatorio_anexos' AND column_name='s3_key') THEN
        ALTER TABLE relatorio_anexos RENAME COLUMN s3_key TO chave_s3;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='relatorio_anexos' AND column_name='chave_s3') THEN
        ALTER TABLE relatorio_anexos ADD COLUMN chave_s3 VARCHAR(255);
    END IF;
END $$;
