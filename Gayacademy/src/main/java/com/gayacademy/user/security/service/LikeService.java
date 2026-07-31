package com.gayacademy.user.security.service;

import com.gayacademy.common.exception.NotFoundException;
import com.gayacademy.user.domain.PostActions.Comentario;
import com.gayacademy.user.domain.PostActions.Like;
import com.gayacademy.user.domain.PostActions.Post;
import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.enums.LikeTargetType;
import com.gayacademy.user.repository.CommentRepository;
import com.gayacademy.user.repository.LikeRepository;
import com.gayacademy.user.repository.PostRepository;
import com.gayacademy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * Alterna o like em um post: se já curtiu, remove; se não curtiu, adiciona.
     *
     * @return {@code true} se o like foi adicionado, {@code false} se foi removido.
     */
    @Transactional
    public boolean toggleLikePost(UUID userId, UUID postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post não encontrado"));

        if (likeRepository.existsByUserAndPostAndTargetType(user, post, LikeTargetType.POST)) {
            likeRepository.deleteByUserAndPostAndTargetType(user, post, LikeTargetType.POST);
            return false;
        }

        likeRepository.save(Like.builder()
                .user(user)
                .post(post)
                .targetType(LikeTargetType.POST)
                .build());
        return true;
    }

    /**
     * Alterna o like em um comentário: se já curtiu, remove; se não curtiu, adiciona.
     *
     * @return {@code true} se o like foi adicionado, {@code false} se foi removido.
     */
    @Transactional
    public boolean toggleLikeComment(UUID userId, UUID commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        Comentario comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comentário não encontrado"));

        if (likeRepository.existsByUserAndCommentAndTargetType(user, comment, LikeTargetType.COMMENT)) {
            likeRepository.deleteByUserAndCommentAndTargetType(user, comment, LikeTargetType.COMMENT);
            return false;
        }

        likeRepository.save(Like.builder()
                .user(user)
                .comment(comment)
                .targetType(LikeTargetType.COMMENT)
                .build());
        return true;
    }

    /** Retorna o total de likes de um post. */
    @Transactional(readOnly = true)
    public long countPostLikes(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post não encontrado"));
        return likeRepository.countByPostAndTargetType(post, LikeTargetType.POST);
    }

    /** Retorna o total de likes de um comentário. */
    @Transactional(readOnly = true)
    public long countCommentLikes(UUID commentId) {
        Comentario comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comentário não encontrado"));
        return likeRepository.countByCommentAndTargetType(comment, LikeTargetType.COMMENT);
    }
}
