package com.gayacademy.chat.dto;

import com.gayacademy.chat.domain.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO REST para envio de mensagem via HTTP (alternativa ao STOMP, ex.: testes).
 * Difere de ChatRequest por não exigir clientMessageId — o servidor gera um.
 */
public record MessageRequest(

        @NotNull(message = "destinatarioId é obrigatório")
        UUID destinatarioId,

        @NotNull(message = "tipo é obrigatório")
        MessageType tipo,

        @NotBlank(message = "conteudo não pode estar vazio")
        @Size(max = 2000)
        String conteudo
) {}
