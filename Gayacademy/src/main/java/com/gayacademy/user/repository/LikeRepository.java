package com.gayacademy.user.repository;

import com.gayacademy.user.domain.PostActions.Comentario;
import com.gayacademy.user.domain.PostActions.Like;
import com.gayacademy.user.domain.PostActions.Post;
import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.enums.LikeTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByUserAndPostAndTargetType(User user, Post post, LikeTargetType targetType);

    boolean existsByUserAndCommentAndTargetType(User user, Comentario comment, LikeTargetType targetType);

    void deleteByUserAndPostAndTargetType(User user, Post post, LikeTargetType targetType);

    void deleteByUserAndCommentAndTargetType(User user, Comentario comment, LikeTargetType targetType);

    long countByPostAndTargetType(Post post, LikeTargetType targetType);

    long countByCommentAndTargetType(Comentario comment, LikeTargetType targetType);
}
