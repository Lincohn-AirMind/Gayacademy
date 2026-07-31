package com.gayacademy.user.dto;

import com.gayacademy.user.domain.PostActions.Post;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de saída com os dados essenciais de um post.
 * totalLikes é calculado pelo serviço e passado na construção.
 */
public record PostResponse(

        UUID idPost,
        UUID userId,
        String nomeExibicao,
        String text,
        OffsetDateTime createdAt,
        long totalLikes
) {
    public static PostResponse from(Post post, long totalLikes) {
        return new PostResponse(
                post.getIdPost(),
                post.getUser().getId(),
                post.getUser().getNomeExibicao(),
                post.getText(),
                post.getCreatedAt(),
                totalLikes
        );
    }
}

