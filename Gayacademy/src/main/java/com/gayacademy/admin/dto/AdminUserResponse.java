package com.gayacademy.admin.dto;

import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.enums.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de visualização de um usuário para o painel administrativo.
 * Expõe apenas dados não sensíveis — sem passwordHash (OWASP A02).
 */
public record AdminUserResponse(

        UUID id,
        String username,
        String email,
        String nomeExibicao,
        Boolean ativo,
        Boolean emailVerificado,
        UserRole role,
        OffsetDateTime createdAt
) {
    public static AdminUserResponse from(User u) {
        return new AdminUserResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getNomeExibicao(),
                u.getAtivo(),
                u.getEmailVerificado(),
                u.getRole(),
                u.getCreatedAt()
        );
    }
}
