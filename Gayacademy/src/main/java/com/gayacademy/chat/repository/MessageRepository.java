package com.gayacademy.chat.repository;

import com.gayacademy.chat.domain.Message;
import com.gayacademy.chat.domain.enums.MessageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends MongoRepository<Message, String> {

    /**
     * Histórico de uma conversa com paginação reversa (CHT-05).
     * Retorna mensagens mais recentes primeiro para paginação "carregar mais acima".
     */
    List<Message> findByConversaIdOrderByCreatedAtDesc(String conversaId, Pageable pageable);

    /**
     * Paginação antes de uma mensagem específica — suporte ao parâmetro antesDeMessageId (CHT-API-05).
     */
    @Query("{ 'conversaId': ?0, '_id': { $lt: ?1 } }")
    List<Message> findBeforeMessage(String conversaId, String antesDeMessageId, Pageable pageable);

    /** Mensagens pendentes/enviadas para um destinatário (retenção offline — CHT-04). */
    List<Message> findByDestinatarioIdAndStatusIn(UUID destinatarioId,
                                                   List<MessageStatus> statuses);

    /** Verifica idempotência pelo clientMessageId (INT-02). */
    boolean existsByClientMessageId(String clientMessageId);
}
