package com.gayacademy.user.security.controller;

import com.gayacademy.common.util.SecurityUtils;
import com.gayacademy.user.security.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Like", description = "Curtir e descurtir posts e comentários")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/v1/posts/{postId}/like")
    @Operation(summary = "Alternar like em um post (adiciona se não curtiu, remove se já curtiu)")
    public ResponseEntity<Map<String, Object>> togglePostLike
    (@PathVariable UUID postId) {
       
        boolean liked = likeService.toggleLikePost
        (SecurityUtils.requireCurrentUserId(), postId);

        long total = likeService.countPostLikes(postId);

        return ResponseEntity.ok(Map.of("liked", liked, "totalLikes", total));
        
    }

    @PostMapping("/api/v1/comments/{commentId}/like")
    @Operation(summary = "Alternar like em um comentário (adiciona se não curtiu, remove se já curtiu)")
    public ResponseEntity<Map<String, Object>> toggleCommentLike(@PathVariable UUID commentId) {
        boolean liked = likeService.toggleLikeComment(SecurityUtils.requireCurrentUserId(), commentId);
        long total = likeService.countCommentLikes(commentId);
        return ResponseEntity.ok(Map.of("liked", liked, "totalLikes", total));
    }

    @GetMapping("/api/v1/posts/{postId}/likes/count")
    @Operation(summary = "Total de likes de um post")
    public ResponseEntity<Map<String, Long>> countPostLikes(@PathVariable UUID postId) {
        return ResponseEntity.ok(Map.of("totalLikes", likeService.countPostLikes(postId)));
    }

    @GetMapping("/api/v1/comments/{commentId}/likes/count")
    @Operation(summary = "Total de likes de um comentário")
    public ResponseEntity<Map<String, Long>> countCommentLikes(@PathVariable UUID commentId) {
        return ResponseEntity.ok(Map.of("totalLikes", likeService.countCommentLikes(commentId)));
    }
}
