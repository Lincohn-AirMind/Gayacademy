package com.gayacademy.chat.repository;

import com.gayacademy.chat.domain.Conversa;
import com.gayacademy.chat.domain.ConversaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversaRepository extends JpaRepository<Conversa, ConversaId> {

    /**
     * Retorna todas as conversas de um usuário (seja ele user1 ou user2),
     * ordenadas pela última atividade — para a lista de conversas do inbox.
     */
    @Query("""
            SELECT c FROM Conversa c
            WHERE c.user1Id = :userId OR c.user2Id = :userId
            ORDER BY c.updatedAt DESC
            """)
    List<Conversa> findByParticipante(@Param("userId") UUID userId);

    /** Busca uma conversa pelo par canônico (user1 < user2). */
    Optional<Conversa> findByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);
}
