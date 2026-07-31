package com.gayacademy.user.domain.PostActions;

import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.enums.LikeTargetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "likes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id", "target_type"}),
        @UniqueConstraint(columnNames = {"user_id", "comment_id", "target_type"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Preenchido quando o like é em um post (targetType = POST). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /** Preenchido quando o like é em um comentário (targetType = COMMENT). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comentario comment;

    /** Indica se o like pertence a um POST ou a um COMMENT. */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 10)
    private LikeTargetType targetType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
