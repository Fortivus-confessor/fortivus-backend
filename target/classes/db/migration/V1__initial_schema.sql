-- V1: Schema Mestre FORTIVUS V2 (Consolidado + Bootstrap Data)
-- Gestão de Combate, Logística e Efetivo com Auditoria Completa

CREATE EXTENSION IF NOT EXISTS "postgis";

-- 1. Gestão Organizacional (Centros e Equipes)
CREATE TABLE public.centro_comando (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    endereco TEXT,
    telefone VARCHAR(50),
    central BOOLEAN DEFAULT FALSE,
    geom GEOMETRY(Point, 4326)
);

CREATE TABLE public.equipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(50) NOT NULL, -- TERRESTRE, AEREO, MAQUINARIO
    centro_comando_id UUID NOT NULL REFERENCES public.centro_comando(id)
);

CREATE TABLE public.usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    primeiro_nome VARCHAR(100),
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    rg VARCHAR(50) UNIQUE,
    matricula VARCHAR(50) UNIQUE,
    posto VARCHAR(100),
    data_nascimento DATE,
    tipo_sanguineo VARCHAR(10),
    foto_url TEXT,
    perfil VARCHAR(50) NOT NULL, -- ROLE_ADMIN, ROLE_CENTRO_COMANDO_CENTRAL, ROLE_CENTRO_COMANDO
    estado_operacional VARCHAR(50) DEFAULT 'DISPONIVEL',
    centro_comando_id UUID REFERENCES public.centro_comando(id),
    equipe_id UUID REFERENCES public.equipes(id)
);

-- 2. Gestão de Ativos (Logística & Frota)
CREATE TABLE public.veiculo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    identificador VARCHAR(100) UNIQUE NOT NULL, -- Placa ou Prefixo
    prefixo VARCHAR(50),
    modelo VARCHAR(255) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    km_atual INTEGER DEFAULT 0,
    foto_url TEXT,
    equipe_id UUID REFERENCES public.equipes(id)
);

CREATE TABLE public.equipamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    identificador VARCHAR(100) UNIQUE NOT NULL, -- Patrimônio
    estado VARCHAR(50) DEFAULT 'OPERANTE',
    equipe_id UUID REFERENCES public.equipes(id)
);

-- 3. Operacional (Combate)
CREATE TABLE public.escala (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipe_id UUID NOT NULL REFERENCES public.equipes(id),
    veiculo_id UUID REFERENCES public.veiculo(id),
    comandante_id UUID NOT NULL REFERENCES public.usuarios(id),
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP,
    ativa BOOLEAN DEFAULT TRUE
);

-- Join table para integrantes da escala
CREATE TABLE public.escala_usuarios (
    escala_id UUID NOT NULL REFERENCES public.escala(id),
    usuario_id UUID NOT NULL REFERENCES public.usuarios(id),
    PRIMARY KEY (escala_id, usuario_id)
);

-- Registro de Checkout de Itens
CREATE TABLE public.checkout_equipamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    escala_id UUID NOT NULL REFERENCES public.escala(id),
    equipamento_id UUID NOT NULL REFERENCES public.equipamento(id),
    responsavel_entrega_id UUID NOT NULL REFERENCES public.usuarios(id),
    data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_devolucao TIMESTAMP,
    responsavel_recebimento_id UUID REFERENCES public.usuarios(id)
);

CREATE TABLE public.ordem_servico (
    id BIGINT PRIMARY KEY, -- SmartId
    categoria VARCHAR(50) NOT NULL,
    localizacao_texto TEXT,
    localizacao_geom GEOMETRY(Geometry, 4326),
    descricao_tarefa TEXT,
    escala_id UUID NOT NULL REFERENCES public.escala(id),
    relator_id UUID NOT NULL REFERENCES public.usuarios(id),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ABERTA'
);

CREATE TABLE public.despacho (
    id BIGINT PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES public.ordem_servico(id),
    status VARCHAR(50) DEFAULT 'EM_ANDAMENTO',
    data_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_fim TIMESTAMP
);

-- 4. Auditoria (Envers)
CREATE SEQUENCE IF NOT EXISTS public.revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE public.revinfo (
    rev INTEGER PRIMARY KEY,
    revtstmp BIGINT,
    usuario_id UUID
);

CREATE TABLE public.usuarios_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES public.revinfo(rev),
    revtype SMALLINT,
    nome VARCHAR(255),
    primeiro_nome VARCHAR(100),
    email VARCHAR(255),
    cpf VARCHAR(14),
    rg VARCHAR(50),
    matricula VARCHAR(50),
    posto VARCHAR(100),
    data_nascimento DATE,
    tipo_sanguineo VARCHAR(10),
    foto_url TEXT,
    perfil VARCHAR(50),
    estado_operacional VARCHAR(50),
    centro_comando_id UUID,
    equipe_id UUID,
    PRIMARY KEY (id, rev)
);

-- 5. Bootstrap Data (Seed Inicial)
-- Inserir Centro de Comando Sede
INSERT INTO public.centro_comando (id, nome, endereco, central) 
VALUES ('00000000-0000-0000-0000-000000000001', 'SEDE CENTRAL FORTIVUS', 'AVENIDA DO COMANDO, 1000 - CUIABÁ', TRUE);

-- Inserir Usuários Administradores (Compatível com Authentik Bootstrap)
INSERT INTO public.usuarios (id, nome, email, perfil, centro_comando_id, estado_operacional)
VALUES (gen_random_uuid(), 'Admin Fortivus', 'admin@fortivus.local', 'ROLE_ADMIN', '00000000-0000-0000-0000-000000000001', 'DISPONIVEL');

INSERT INTO public.usuarios (id, nome, email, perfil, centro_comando_id, estado_operacional)
VALUES (gen_random_uuid(), 'akadmin', 'akadmin@localhost', 'ROLE_ADMIN', '00000000-0000-0000-0000-000000000001', 'DISPONIVEL');
