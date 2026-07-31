package com.gayacademy.chat.dto;

import com.gayacademy.chat.domain.Message;
import com.gayacademy.chat.domain.enums.MessageStatus;
import com.gayacademy.chat.domain.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de mensagem enviada ao destinatário via /user/queue/mensagens (CHT-API-04)
 * e também utilizado nas respostas REST do histórico.
 */
public record MessageResponse(

        String serverMessageId,
        UUID remetenteId,
        String nomeExibicao,
        MessageType tipo,
        String conteudo,
        MessageStatus status,
        Instant createdAt
) {
    public static MessageResponse from(Message m, String nomeExibicao) {
        return new MessageResponse(
                m.getId(),
                m.getRemetenteId(),
                nomeExibicao,
                m.getTipo(),
                m.getConteudo(),
                m.getStatus(),
                m.getCreatedAt()
        );
    }
}
