package com.gayacademy.user.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username e obrigatorio")
        @Pattern(regexp = "^[a-zA-Z0-9_.]{3,30}$",
                message = "Username deve ter 3-30 caracteres (letras, numeros, _ ou .)")
        String username,

        @NotBlank(message = "Email e obrigatorio")
        @Email(message = "Email invalido")
        @Size(max = 180, message = "Email muito longo")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
        String senha,

        @NotBlank(message = "Nome de exibicao e obrigatorio")
        @Size(min = 2, max = 80, message = "Nome de exibicao deve ter entre 2 e 80 caracteres")
        String nomeExibicao
) {}