package com.gayacademy.user.security.controller;

import com.gayacademy.common.util.SecurityUtils;
import com.gayacademy.user.dto.BlockResponse;
import com.gayacademy.user.security.service.BlockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Block", description = "Bloquear e desbloquear usuarios")
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/{id}/block")
    @Operation(summary = "Bloquear um usuario (remove follow nos dois sentidos)")
    public ResponseEntity<BlockResponse> bloquear(@PathVariable UUID id) {
        return ResponseEntity.ok(blockService.bloquear(SecurityUtils.requireCurrentUserId(), id));
    }

    @DeleteMapping("/{id}/block")
    @Operation(summary = "Desbloquear um usuario")
    public ResponseEntity<BlockResponse> desbloquear(@PathVariable UUID id) {
        return ResponseEntity.ok(blockService.desbloquear(SecurityUtils.requireCurrentUserId(), id));
    }
}