package com.gayacademy.user.security.service;

import com.gayacademy.common.util.SecurityUtils;
import com.gayacademy.user.dto.CommentRequest;
import com.gayacademy.user.dto.CommentResponse;
import com.gayacademy.user.domain.PostActions.Comentario;
import com.gayacademy.user.repository.PostRepository;
import com.gayacademy.user.repository.UserRepository;
import com.gayacademy.common.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comentários em posts")
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * Lista comentários de um post com paginação por cursor.
     * Sem cursor retorna a primeira página; com cursor retorna a próxima.
     */
    @GetMapping("/{postId}/comments")
    @Operation(summary = "Listar comentários de um post (paginação por cursor)")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable UUID postId,
            @RequestParam(required = false) OffsetDateTime cursor
    ) {
        List<CommentResponse> page;

        if (cursor == null) {
            page = commentService.getFirstPage(postId)
                    .stream()
                    .map(CommentResponse::from)
                    .toList();
        } else {
            page = commentService.getPageUntilIdPost(postId, cursor)
                    .stream()
                    .map(CommentResponse::from)
                    .toList();
        }

        return ResponseEntity.ok(page);
    }

    /** Cria um comentário no post indicado pelo body. */
    @PostMapping("/comments")
    @Operation(summary = "Criar um comentário em um post")
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request
    ) {
        var post = postRepository.findById(request.postId())
                .orElseThrow(() -> new NotFoundException("Post não encontrado"));
        var user = userRepository.findById(SecurityUtils.requireCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Comentario comentario = Comentario.builder()
                .post(post)
                .user(user)
                .text(request.text())
                .build();

        boolean saved = commentService.saveComment(comentario);
        if (!saved) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(201).body(CommentResponse.from(comentario));
    }

    /** Remove um comentário pelo seu ID. */
    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Deletar um comentário")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        boolean deleted = commentService.deleteComment(commentId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

