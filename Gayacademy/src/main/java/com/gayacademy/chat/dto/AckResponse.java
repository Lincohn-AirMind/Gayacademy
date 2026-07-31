package com.gayacademy.chat.dto;

import com.gayacademy.chat.domain.enums.MessageStatus;

import java.time.Instant;

/**
 * ACK enviado ao remetente via /user/queue/ack após o servidor persistir a mensagem (CHT-API-03).
 * Permite que o cliente atualize o status local de PENDING → SENT (optimistic update — INT-05).
 */
public record AckResponse(

        /** ID gerado pelo cliente — permite correlacionar com a mensagem local. */
        String clientMessageId,

        /** ID atribuído pelo servidor após persistência. */
        String serverMessageId,

        MessageStatus status,

        Instant timestamp
) {}
