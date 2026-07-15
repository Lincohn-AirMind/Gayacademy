package com.gayacademy.user;

import com.gayacademy.common.exception.BusinessException;
import com.gayacademy.common.exception.NotFoundException;
import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.UserProfile;
import com.gayacademy.user.domain.enums.Privacidade;
import com.gayacademy.user.dto.UpdateProfileRequest;
import com.gayacademy.user.dto.UserProfileResponse;
import com.gayacademy.user.repository.BlockRepository;
import com.gayacademy.user.repository.FollowRepository;
import com.gayacademy.user.repository.UserProfileRepository;
import com.gayacademy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowRepository followRepository;
    private final BlockRepository blockRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getMeuPerfil(UUID meuId) {
        return getPerfilCompleto(meuId, meuId);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getPerfilPublico(UUID targetId, UUID meuId) {
        // Regra de Bloqueio: Se houver bloqueio em qualquer direção, finge que não existe (404)
        if (meuId != null && blockRepository.existsBlockBetween(meuId, targetId)) {
            throw new NotFoundException("Usuario nao encontrado");
        }

        User user = userRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));
        UserProfile profile = userProfileRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException("Perfil nao encontrado"));

        long seguidores = followRepository.countByFolloweeId(targetId);
        long seguindo = followRepository.countByFollowerId(targetId);
        
        boolean euSigo = meuId != null && followRepository.existsByFollowerIdAndFolloweeId(meuId, targetId);

        // Regra de Privacidade
        if (profile.getPrivacidade() == Privacidade.PRIVADO && !targetId.equals(meuId) && !euSigo) {
            return UserProfileResponse.restricted(user, profile, seguidores, seguindo);
        }

        return UserProfileResponse.full(user, profile, seguidores, seguindo, euSigo);
    }

    @Transactional
    public UserProfileResponse atualizarPerfil(UUID meuId, UpdateProfileRequest request) {
        User user = userRepository.findById(meuId).orElseThrow();
        UserProfile profile = userProfileRepository.findById(meuId).orElseThrow();

        if (request.nomeExibicao() != null) user.setNomeExibicao(request.nomeExibicao());
        if (request.bio() != null) profile.setBio(request.bio());
        if (request.avatarUrl() != null) profile.setAvatarUrl(request.avatarUrl());
        if (request.alturaCm() != null) profile.setAlturaCm(request.alturaCm());
        if (request.pesoKg() != null) profile.setPesoKg(request.pesoKg());
        if (request.objetivo() != null) profile.setObjetivo(request.objetivo());
        if (request.privacidade() != null) profile.setPrivacidade(request.privacidade());

        userRepository.save(user);
        userProfileRepository.save(profile);

        return getMeuPerfil(meuId);
    }

    private UserProfileResponse getPerfilCompleto(UUID targetId, UUID meuId) {
        User user = userRepository.findById(targetId).orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));
        UserProfile profile = userProfileRepository.findById(targetId).orElseThrow();
        
        long seguidores = followRepository.countByFolloweeId(targetId);
        long seguindo = followRepository.countByFollowerId(targetId);
        boolean euSigo = followRepository.existsByFollowerIdAndFolloweeId(meuId, targetId);

        return UserProfileResponse.full(user, profile, seguidores, seguindo, euSigo);
    }
}