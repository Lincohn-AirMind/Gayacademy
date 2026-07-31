package com.gayacademy.user.security.controller;

import com.gayacademy.common.util.SecurityUtils;
import com.gayacademy.user.dto.FollowRequestResponse;
import com.gayacademy.user.dto.FollowResponse;
import com.gayacademy.user.security.service.FollowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "Seguir, deixar de seguir e gerenciar pedidos")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{id}/follow")
    @Operation(summary = "Seguir um usuario (cria pedido se for privado)")
    public ResponseEntity<FollowResponse> seguir(@PathVariable UUID id) {
        return ResponseEntity.ok(followService.seguir(SecurityUtils.requireCurrentUserId(), id));
    }

    @DeleteMapping("/{id}/follow")
    @Operation(summary = "Deixar de seguir um usuario")
    public ResponseEntity<FollowResponse> deixarDeSeguir(@PathVariable UUID id) {
        return ResponseEntity.ok(followService.deixarDeSeguir(SecurityUtils.requireCurrentUserId(), id));
    }

    @GetMapping("/me/follow-requests")
    @Operation(summary = "Lista pedidos de follow pendentes recebidos")
    public ResponseEntity<List<FollowRequestResponse>> listarPedidos() {
        return ResponseEntity.ok(followService.listarPedidosRecebidos(SecurityUtils.requireCurrentUserId()));
    }

    @PostMapping("/me/follow-requests/{requestId}/accept")
    @Operation(summary = "Aceita um pedido de follow")
    public ResponseEntity<FollowResponse> aceitar(@PathVariable UUID requestId) {
        return ResponseEntity.ok(followService.aceitarPedido(SecurityUtils.requireCurrentUserId(), requestId));
    }

    @PostMapping("/me/follow-requests/{requestId}/reject")
    @Operation(summary = "Rejeita um pedido de follow")
    public ResponseEntity<FollowResponse> rejeitar(@PathVariable UUID requestId) {
        return ResponseEntity.ok(followService.rejeitarPedido(SecurityUtils.requireCurrentUserId(), requestId));
    }
}