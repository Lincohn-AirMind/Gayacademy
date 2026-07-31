package com.gayacademy.admin.repository;

import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Repositório de administração — consultas privilegiadas sobre usuários.
 * Acesso restrito ao módulo admin (ROLE_ADMIN).
 */
public interface AdmRepository extends JpaRepository<User, UUID> {

    /**
     * Lista usuários com filtro opcional de username/email e paginação.
     *
     * <p>Usa query nativa com {@code ILIKE} + índices GIN trigram (pg_trgm),
     * evitando full scan mesmo com wildcard no início ({@code %texto%}).
     * {@code LOWER()} foi removido — {@code ILIKE} já é case-insensitive no PostgreSQL.
     */
    @Query(value = """
            SELECT * FROM users
            WHERE (:filtro IS NULL
                OR username ILIKE '%' || :filtro || '%'
                OR email    ILIKE '%' || :filtro || '%')
            ORDER BY created_at DESC
            """, nativeQuery = true)
    Page<User> buscarUsuarios(@Param("filtro") String filtro, Pageable pageable);

    /** Conta usuários por status ativo para dashboard. */
    long countByAtivo(Boolean ativo);

    /** Conta usuários por role. */
    long countByRole(UserRole role);
}
