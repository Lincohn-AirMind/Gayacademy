package com.gayacademy.user.dto;

import com.gayacademy.user.domain.PostActions.Comentario;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de saída com os dados essenciais de um comentário.
 */
public record CommentResponse(

        UUID idComment,
        UUID postId,
        UUID userId,
        String nomeExibicao,
        String text,
        OffsetDateTime createdAt
) {
    public static CommentResponse from(Comentario c) {
        return new CommentResponse(
                c.getIdComment(),
                c.getPost().getIdPost(),
                c.getUser().getId(),
                c.getUser().getNomeExibicao(),
                c.getText(),
                c.getCreatedAt()
        );
    }
}
