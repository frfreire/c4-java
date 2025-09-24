---
-- Script de inicialização do banco de dados

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


    private boolean ativo = true;
    private boolean contaNaoExpirada = true;
    private boolean contaNaoBloqueada = true;
    private boolean credencialNaoExpirada = true;
    private LocalDateTime criadoEm = LocalDateTime.now();
    private LocalDateTime ultimoLogin;

-- Criar tabela de usuários (caso não exista)
CREATE TABLE IF NOT EXISTS usuarios (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USUARIO',
    ativo BOOLEAN NOT NULL DEFAULT true,
    conta_nao_expirada BOOLEAN NOT NULL DEFAULT true,
    conta_nao_bloqueada BOOLEAN NOT NULL DEFAULT true,
    credencial_nao_expirada BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_login TIMESTAMP,
    CONSTRAINT check_role CHECK (role IN ('USUARIO', 'ADMIN', 'MODERADOR'))
    );

-- Criar índices para performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(criadoEm);

-- Inserir usuário administrador padrão
-- Senha: admin123 (BCrypt hash)
INSERT INTO users (username, email, password, role, ativo) VALUES
    ('admin', 'admin@tabajara.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDDmxTlVEukvq1dXKx9vfmHDNk9U8e4a', 'ADMIN', true)
    ON CONFLICT (username) DO NOTHING;

-- Inserir usuário comum para testes
-- Senha: user123 (BCrypt hash)
INSERT INTO users (username, email, password, role, enabled) VALUES
    ('fabricio', 'fabricio@tabajara.com', '$2a$10$b.3l7/3ZvJ7U7XKxOQE2YuE6aTa3L1zB5s3F1J5u4M8xF2aD1bF3G', 'USUARIO', true)
    ON CONFLICT (username) DO NOTHING;

-- Criar tabela de logs de auditoria (opcional)
CREATE TABLE IF NOT EXISTS audit_logs (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    details TEXT,
    ip_address INET,
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);

-- Criar view para relatórios de usuários
CREATE OR REPLACE VIEW user_stats AS
SELECT
    role,
    COUNT(*) as total_users,
    COUNT(CASE WHEN ativo = true THEN 1 END) as active_users,
    COUNT(CASE WHEN ultimo_login >= CURRENT_DATE - INTERVAL '30 days' THEN 1 END) as recent_logins
FROM users
GROUP BY role;

-- Comentários para documentação
COMMENT ON TABLE usuarios IS 'Tabela principal de usuários do sistema';
COMMENT ON COLUMN usuarios.username IS 'Nome de usuário único para login';
COMMENT ON COLUMN usuarios.email IS 'Email único do usuário';
COMMENT ON COLUMN usuarios.password IS 'Senha criptografada com BCrypt';
COMMENT ON COLUMN usuarios.role IS 'Papel do usuário no sistema (USER, ADMIN, MODERATOR)';
COMMENT ON COLUMN usuarios.ultimo_login IS 'Timestamp do último login realizado';

COMMENT ON TABLE audit_logs IS 'Logs de auditoria para rastreamento de ações';
COMMENT ON VIEW user_stats IS 'Estatísticas agregadas de usuários por papel';