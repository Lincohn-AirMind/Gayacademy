package com.gayacademy.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para suspender ou reativar um usuário (PUT /adm/users/{id}/status).
 */
public record AdminSuspendRequest(

        @NotNull(message = "O campo 'ativo' é obrigatório")
        Boolean ativo,

        @Size(max = 300, message = "Motivo não pode exceder 300 caracteres")
        String motivo
) {}
