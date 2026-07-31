package com.gayacademy.chat.dto;

import com.gayacademy.chat.domain.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Payload STOMP enviado pelo cliente ao publicar em /app/chat.enviar (CHT-API-02).
 */
public record ChatRequest(

        /** ID gerado pelo cliente para idempotência (INT-02). */
        @NotBlank(message = "clientMessageId é obrigatório")
        String clientMessageId,

        @NotNull(message = "destinatarioId é obrigatório")
        UUID destinatarioId,

        @NotNull(message = "tipo é obrigatório")
        MessageType tipo,

        @NotBlank(message = "conteudo não pode estar vazio")
        @Size(max = 2000)
        String conteudo
) {}
