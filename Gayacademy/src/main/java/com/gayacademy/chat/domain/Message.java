package com.gayacademy.chat.domain;

import com.gayacademy.chat.domain.enums.MessageStatus;
import com.gayacademy.chat.domain.enums.MessageType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Documento MongoDB que representa uma mensagem de chat.
 * O histórico fica no MongoDB (CHT-NF02).
 */
@Document(collection = "messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private String id;

    /**
     * Identificador canônico da conversa: menor UUID + ":" + maior UUID.
     * Permite buscar todas as mensagens de ambas as direções com um único índice.
     */
    @Indexed
    private String conversaId;

    private UUID remetenteId;

    private UUID destinatarioId;

    /**
     * ID gerado pelo cliente — garante idempotência (INT-02).
     */
    @Indexed(unique = true, sparse = true)
    private String clientMessageId;

    private MessageType tipo;

    private String conteudo;

    /** URL da mídia quando tipo for IMAGE ou VIDEO. */
    private String mediaUrl;

    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    @CreatedDate
    private Instant createdAt;
}
