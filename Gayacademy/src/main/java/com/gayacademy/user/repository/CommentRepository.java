package com.gayacademy.user.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.gayacademy.user.domain.PostActions.Comentario;

public interface CommentRepository
 extends JpaRepository<Comentario, UUID> {
//lista os comentários com id
  Comentario findByIdComment(UUID idComment);

  //achar a primeira page
  @Query("""
     SELECT c FROM Comentario c
     JOIN FETCH c.post 
      JOIN FETCH c.user 
     WHERE c.post.idPost = :idPost
     ORDER BY c.createdAt ASC
      """)
  List<Comentario> findFirstPage(@Param("idPost")
   UUID idPost, Pageable pageable);


//pega a quantidade de comentarios definida no cursor(20,10,etc...) em 1 consulta só
//evitando o problema N+1.

@Query("""
            SELECT c FROM Comentario c 
            JOIN FETCH c.user 
             JOIN FETCH c.post 
            WHERE c.post.idPost = :postId 
              AND c.createdAt > :cursor
            ORDER BY c.createdAt ASC
            """)
    List<Comentario> findNextCommentsPage(
            @Param("postId") UUID postId, 
            @Param("cursor") OffsetDateTime cursor, 
            Pageable pageable
    );

//delete especifico
@Transactional
@Modifying
@Query("""
    DELETE FROM Comentario c WHERE c.idComment = :idComment
    """)
int deleteComment(@Param("idComment") UUID idComment);


}
