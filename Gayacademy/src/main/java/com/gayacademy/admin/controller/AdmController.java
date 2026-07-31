package com.gayacademy.admin.controller;

import com.gayacademy.admin.dto.AdminSuspendRequest;
import com.gayacademy.admin.dto.AdminUserResponse;
import com.gayacademy.admin.service.AdmService;
import com.gayacademy.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller administrativo — acesso restrito a ROLE_ADMIN.
 * Admin: apenas GET, PUT, DELETE. Sem POST (sem criação de conteúdo).
 */
@RestController
@RequestMapping("/adm/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
@Tag(name = "Admin", description = "Painel de administração — gestão de usuários")
public class AdmController {

    private final AdmService admService;

    /**
     * Lista usuários com busca opcional e paginação (GNF-01, GNF-02).
     *
     * @param filtro pesquisa por username ou e-mail (opcional).
     * @param page   número da página (0-based).
     */
    @GetMapping
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<Page<AdminUserResponse>> listar(
            @RequestParam(required = false) String filtro,
            @RequestParam(defaultValue = "0") @Min(0) @Max(500) int page
    ) {
        return ResponseEntity.ok(admService.listarUsuarios(filtro, page));
    }

    /**
     * Retorna os dados de um usuário específico.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<AdminUserResponse> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(admService.buscarUsuario(id));
    }

    /**
     * Suspende ou reativa um usuário (campo ativo).
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Suspender ou reativar um usuário")
    public ResponseEntity<AdminUserResponse> atualizarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AdminSuspendRequest request
    ) {
        return ResponseEntity.ok(
                admService.atualizarStatus(SecurityUtils.requireCurrentUserId(), id, request));
    }

    /**
     * Remove permanentemente um usuário comum do sistema.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um usuário comum")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        admService.deletarUsuario(SecurityUtils.requireCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove um post de qualquer usuário (moderação).
     */
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "Deletar post de qualquer usuário (moderação)")
    public ResponseEntity<Void> deletarPost(@PathVariable UUID postId) {
        admService.deletarPost(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove um comentário de qualquer usuário (moderação).
     */
    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Deletar comentário de qualquer usuário (moderação)")
    public ResponseEntity<Void> deletarComentario(@PathVariable UUID commentId) {
        admService.deletarComentario(commentId);
        return ResponseEntity.noContent().build();
    }
}
