package com.gayacademy.user.dto;

import java.util.UUID;

public record FollowResponse(
        UUID followerId,
        UUID followeeId,
        String status,
        String mensagem
) {
    public static FollowResponse seguindo(UUID follower, UUID followee) {
        return new FollowResponse(follower, followee, "SEGUINDO", "Voce agora segue este usuario");
    }

    public static FollowResponse pendente(UUID follower, UUID followee) {
        return new FollowResponse(follower, followee, "PENDENTE", "Pedido de follow enviado e aguardando aprovacao");
    }

    public static FollowResponse desfeito(UUID follower, UUID followee) {
        return new FollowResponse(follower, followee, "REMOVIDO", "Voce deixou de seguir este usuario");
    }
}