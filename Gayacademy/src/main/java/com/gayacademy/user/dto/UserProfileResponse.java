package com.gayacademy.user.dto;

import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.UserProfile;
import com.gayacademy.user.domain.enums.Objetivo;
import com.gayacademy.user.domain.enums.Privacidade;

import java.math.BigDecimal;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String nomeExibicao,
        String avatarUrl,
        String bio,
        Short alturaCm,
        BigDecimal pesoKg,
        Objetivo objetivo,
        Privacidade privacidade,
        boolean ePrivado,
        boolean eSeguidor,
        long totalSeguidores,
        long totalSeguindo
) {
    public static UserProfileResponse full(User u, UserProfile p, long seguidores, long seguindo, boolean eSeguidor) {
        return new UserProfileResponse(
                u.getId(), u.getUsername(), u.getNomeExibicao(),
                p.getAvatarUrl(), p.getBio(), p.getAlturaCm(), p.getPesoKg(),
                p.getObjetivo(), p.getPrivacidade(),
                false, eSeguidor, seguidores, seguindo
        );
    }

    public static UserProfileResponse restricted(User u, UserProfile p, long seguidores, long seguindo) {
        return new UserProfileResponse(
                u.getId(), u.getUsername(), u.getNomeExibicao(),
                p.getAvatarUrl(), null, null, null, null,
                p.getPrivacidade(), true, false, seguidores, seguindo
        );
    }
}