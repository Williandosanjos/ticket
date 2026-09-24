CREATE TABLE ticket (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo VARCHAR(100) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    email_Usuario VARCHAR(255) NOT NULL,
    prioridade VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTO',
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);