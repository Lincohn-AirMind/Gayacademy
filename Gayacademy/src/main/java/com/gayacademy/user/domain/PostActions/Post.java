package com.gayacademy.user.domain.PostActions;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.gayacademy.user.domain.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idPost")
public class Post {

    // essa classe serve para tratar dos posts dos usuarios, com textinhos
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idPost;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String text;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
        cascade = CascadeType.REMOVE
    )
    private List<Like> likes;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY,
        cascade = CascadeType.REMOVE
    )
    private List<Comentario> comentarios;
}
