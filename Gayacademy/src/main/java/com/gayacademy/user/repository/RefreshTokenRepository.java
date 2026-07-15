package com.gayacademy.user.repository;

import com.gayacademy.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
           UPDATE RefreshToken r
           SET r.revokedAt = :agora
           WHERE r.userId = :userId
             AND r.revokedAt IS NULL
           """)
    int revogarTodosDoUsuario(@Param("userId") UUID userId, @Param("agora") OffsetDateTime agora);
}