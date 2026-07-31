package com.gayacademy.chat.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Documento MongoDB para notificações (mensagens recebidas por usuários offline,
 * likes, follows, etc.). Usado para retenção offline (CHT-04).
 */
@Document(collection = "notificacoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {

    @Id
    private String id;

    /** Usuário que deve receber a notificação. */
    @Indexed
    private UUID destinatarioId;

    /**
     * Tipo da notificação: MESSAGE, LIKE, FOLLOW, FOLLOW_REQUEST, etc.
     */
    private String tipo;

    private String titulo;

    private String conteudo;

    /** Referência ao recurso relacionado (postId, conversaId, userId…). */
    private String referenciaId;

    @Builder.Default
    private boolean lida = false;

    @CreatedDate
    private Instant createdAt;
}
