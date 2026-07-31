package com.gayacademy.chat.dto;

import com.gayacademy.chat.domain.Notificacao;

import java.time.Instant;

public record NotificacaoResponse(
        String id,
        String tipo,
        String titulo,
        String conteudo,
        String referenciaId,
        boolean lida,
        Instant createdAt
) {
    public static NotificacaoResponse from(Notificacao n) {
        return new NotificacaoResponse(
                n.getId(),
                n.getTipo(),
                n.getTitulo(),
                n.getConteudo(),
                n.getReferenciaId(),
                n.isLida(),
                n.getCreatedAt()
        );
    }
}
