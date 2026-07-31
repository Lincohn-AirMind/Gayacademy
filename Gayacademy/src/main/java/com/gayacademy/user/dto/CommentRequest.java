package com.gayacademy.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO de entrada para criação de um comentário.
 */
public record CommentRequest(

        @NotNull(message = "O ID do post é obrigatório")
        UUID postId,

        @NotBlank(message = "O texto do comentário não pode estar em branco")
        @Size(min = 1, max = 500, message = "O comentário deve ter entre 1 e 500 caracteres")
        String text
) {}
