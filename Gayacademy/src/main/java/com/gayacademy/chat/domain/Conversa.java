package com.gayacademy.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidade JPA (PostgreSQL) que rastreia metadados de uma conversa entre dois usuários.
 * user1Id < user2Id (UUIDs comparados como String para ordenação canônica),
 * garantindo que cada par seja único.
 */
@Entity
@Table(
    name = "conversas",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
)
@IdClass(ConversaId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversa {

    @Id
    @Column(name = "user1_id", nullable = false)
    private UUID user1Id;

    @Id
    @Column(name = "user2_id", nullable = false)
    private UUID user2Id;

    @Column(name = "ultima_mensagem", length = 200)
    private String ultimaMensagem;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Retorna o ID canônico da conversa usado no MongoDB (menor UUID : maior UUID).
     */
    public String conversaId() {
        return user1Id.toString().compareTo(user2Id.toString()) < 0
                ? user1Id + ":" + user2Id
                : user2Id + ":" + user1Id;
    }

    /** Constrói uma Conversa garantindo que user1Id < user2Id. */
    public static Conversa entre(UUID a, UUID b) {
        boolean aFirst = a.toString().compareTo(b.toString()) < 0;
        return Conversa.builder()
                .user1Id(aFirst ? a : b)
                .user2Id(aFirst ? b : a)
                .build();
    }
}
