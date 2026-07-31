-- =====================================================
-- V3 — Role de admin nos usuários + tabela de conversas
-- =====================================================

-- ============ Adicionar role aos usuários ============
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(10) NOT NULL DEFAULT 'USER'
        CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN'));

-- ============ TABELA: conversas ============
-- Rastreia metadados (última mensagem, timestamp) de conversas 1-a-1.
-- user1_id < user2_id (constraint garante par canônico sem duplicatas).
CREATE TABLE IF NOT EXISTS conversas (
    user1_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    ultima_mensagem VARCHAR(200),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user1_id, user2_id),
    CONSTRAINT conversas_ordered CHECK (user1_id < user2_id)
);

CREATE INDEX IF NOT EXISTS idx_conversas_user1 ON conversas (user1_id);
CREATE INDEX IF NOT EXISTS idx_conversas_user2 ON conversas (user2_id);
CREATE INDEX IF NOT EXISTS idx_conversas_updated ON conversas (updated_at DESC);
