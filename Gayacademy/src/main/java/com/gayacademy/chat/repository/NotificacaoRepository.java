package com.gayacademy.chat.repository;

import com.gayacademy.chat.domain.Notificacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacaoRepository extends MongoRepository<Notificacao, String> {

    /** Notificações não lidas de um usuário, mais recentes primeiro. */
    List<Notificacao> findByDestinatarioIdAndLidaFalseOrderByCreatedAtDesc(
            UUID destinatarioId, Pageable pageable);

    /** Todas as notificações do usuário com paginação. */
    List<Notificacao> findByDestinatarioIdOrderByCreatedAtDesc(
            UUID destinatarioId, Pageable pageable);

    /** Contagem de notificações não lidas — usado para badge. */
    long countByDestinatarioIdAndLidaFalse(UUID destinatarioId);

    /** Marca todas as notificações de um usuário como lidas. */
    List<Notificacao> findByDestinatarioIdAndLidaFalse(UUID destinatarioId);
}
