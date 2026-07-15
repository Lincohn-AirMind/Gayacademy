package com.gayacademy.user.dto;

import java.util.UUID;

public record BlockResponse(
        UUID blockerId,
        UUID blockedId,
        String status,
        String mensagem
) {
    public static BlockResponse bloqueado(UUID blocker, UUID blocked) {
        return new BlockResponse(blocker, blocked, "BLOQUEADO", "Usuario bloqueado com sucesso");
    }

    public static BlockResponse desbloqueado(UUID blocker, UUID blocked) {
        return new BlockResponse(blocker, blocked, "DESBLOQUEADO", "Usuario desbloqueado");
    }
}