package com.gayacademy.user.domain;

import com.gayacademy.user.domain.enums.FollowRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "follow_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FollowRequest {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "requester_id", nullable = false, columnDefinition = "uuid")
    private UUID requesterId;

    @Column(name = "target_id", nullable = false, columnDefinition = "uuid")
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FollowRequestStatus status = FollowRequestStatus.PENDENTE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;
}