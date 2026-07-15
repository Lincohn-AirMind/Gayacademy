package com.gayacademy.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "blocks")
@IdClass(BlockId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @Column(name = "blocker_id", columnDefinition = "uuid")
    private UUID blockerId;

    @Id
    @Column(name = "blocked_id", columnDefinition = "uuid")
    private UUID blockedId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}