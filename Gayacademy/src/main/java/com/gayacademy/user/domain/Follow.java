package com.gayacademy.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "follows")
@IdClass(FollowId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    @Id
    @Column(name = "follower_id", columnDefinition = "uuid")
    private UUID followerId;

    @Id
    @Column(name = "followee_id", columnDefinition = "uuid")
    private UUID followeeId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}