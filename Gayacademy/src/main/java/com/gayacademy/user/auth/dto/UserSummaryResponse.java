package com.gayacademy.user.auth.dto;

import com.gayacademy.user.domain.User;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String username,
        String email,
        String nomeExibicao
) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNomeExibicao()
        );
    }
}