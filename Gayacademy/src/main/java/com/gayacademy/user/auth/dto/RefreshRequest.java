package com.gayacademy.user.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

        @NotBlank(message = "Refresh token e obrigatorio")
        String refreshToken
) {}