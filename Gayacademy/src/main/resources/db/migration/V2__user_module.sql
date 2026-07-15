-- =====================================================
-- V2 — Módulo de Usuário, Perfil, Follow, Block
-- =====================================================

-- ============ TABELA: users ============
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username        VARCHAR(30) NOT NULL,
    email           VARCHAR(180) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    nome_exibicao   VARCHAR(80) NOT NULL,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    email_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT username_format CHECK (username ~ '^[a-zA-Z0-9_.]{3,30}$')
);

-- Índices únicos case-insensitive (substituem o CITEXT)
CREATE UNIQUE INDEX uq_users_email_lower    ON users (LOWER(email));
CREATE UNIQUE INDEX uq_users_username_lower ON users (LOWER(username));

-- ============ TABELA: user_profiles ============
CREATE TABLE user_profiles (
    user_id         UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    bio             VARCHAR(280),
    avatar_url      VARCHAR(500),
    altura_cm       SMALLINT CHECK (altura_cm BETWEEN 50 AND 280),
    peso_kg         NUMERIC(5,2) CHECK (peso_kg BETWEEN 20 AND 400),
    objetivo        VARCHAR(40),
    privacidade     VARCHAR(20) NOT NULL DEFAULT 'PUBLICO'
                    CHECK (privacidade IN ('PUBLICO', 'PRIVADO')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============ TABELA: follows ============
CREATE TABLE follows (
    follower_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    followee_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (follower_id, followee_id),
    CONSTRAINT no_self_follow CHECK (follower_id <> followee_id)
);

CREATE INDEX idx_follows_followee ON follows (followee_id);
CREATE INDEX idx_follows_follower ON follows (follower_id);

-- ============ TABELA: follow_requests ============
CREATE TABLE follow_requests (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requester_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                    CHECK (status IN ('PENDENTE', 'ACEITO', 'REJEITADO', 'CANCELADO')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at     TIMESTAMPTZ,
    CONSTRAINT no_self_request CHECK (requester_id <> target_id),
    CONSTRAINT uq_request_pendente UNIQUE (requester_id, target_id, status)
);

CREATE INDEX idx_follow_req_target ON follow_requests (target_id, status);

-- ============ TABELA: blocks ============
CREATE TABLE blocks (
    blocker_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (blocker_id, blocked_id),
    CONSTRAINT no_self_block CHECK (blocker_id <> blocked_id)
);

CREATE INDEX idx_blocks_blocked ON blocks (blocked_id);

-- ============ TABELA: refresh_tokens ============
CREATE TABLE refresh_tokens (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    expires_at      TIMESTAMPTZ NOT NULL,
    revoked_at      TIMESTAMPTZ,
    replaced_by_id  UUID REFERENCES refresh_tokens(id),
    user_agent      VARCHAR(255),
    ip_address      VARCHAR(45),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_user ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_expires ON refresh_tokens (expires_at);