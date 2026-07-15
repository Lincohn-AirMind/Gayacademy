package com.gayacademy.user.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Identificador e obrigatorio (email ou username)")
        String identificador,

        @NotBlank(message = "Senha e obrigatoria")
        String senha
) {}