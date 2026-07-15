package com.gayacademy.user.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UserSummaryResponse usuario
) {
    public static AuthResponse of(String accessToken, String refreshToken,
                                  long expiresInSeconds, UserSummaryResponse usuario) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds, usuario);
    }
}