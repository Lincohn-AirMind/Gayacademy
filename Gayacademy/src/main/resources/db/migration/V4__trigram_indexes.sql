-- =====================================================
-- V4 — Índices trigram para busca eficiente de usuários
-- =====================================================

-- Habilita a extensão pg_trgm (suporte a ILIKE com índice GIN)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Índices GIN trigram em username e email.
-- Com esses índices, ILIKE '%texto%' usa índice em vez de full scan.
CREATE INDEX IF NOT EXISTS idx_users_username_trgm
    ON users USING GIN (username gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_users_email_trgm
    ON users USING GIN (email gin_trgm_ops);
