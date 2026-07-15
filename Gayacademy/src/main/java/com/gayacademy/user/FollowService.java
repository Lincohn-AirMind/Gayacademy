package com.gayacademy.user;

import com.gayacademy.common.exception.BusinessException;
import com.gayacademy.common.exception.NotFoundException;
import com.gayacademy.user.domain.Follow;
import com.gayacademy.user.domain.FollowRequest;
import com.gayacademy.user.domain.User;
import com.gayacademy.user.domain.UserProfile;
import com.gayacademy.user.domain.enums.FollowRequestStatus;
import com.gayacademy.user.domain.enums.Privacidade;
import com.gayacademy.user.dto.FollowRequestResponse;
import com.gayacademy.user.dto.FollowResponse;
import com.gayacademy.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final BlockRepository blockRepository;

    @Transactional
    public FollowResponse seguir(UUID meuId, UUID targetId) {
        if (meuId.equals(targetId)) {
            throw new BusinessException("Voce nao pode seguir a si mesmo", HttpStatus.BAD_REQUEST);
        }

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        if (blockRepository.existsBlockBetween(meuId, targetId)) {
            throw new BusinessException("Acao nao permitida", HttpStatus.FORBIDDEN);
        }

        // Idempotencia: ja segue?
        if (followRepository.existsByFollowerIdAndFolloweeId(meuId, targetId)) {
            return FollowResponse.seguindo(meuId, targetId);
        }

        UserProfile profile = userProfileRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException("Perfil nao encontrado"));

        // Perfil PRIVADO: cria pedido pendente
        if (profile.getPrivacidade() == Privacidade.PRIVADO) {
            boolean jaTemPedidoPendente = followRequestRepository
                    .existsByRequesterIdAndTargetIdAndStatus(meuId, targetId, FollowRequestStatus.PENDENTE);
            if (jaTemPedidoPendente) {
                return FollowResponse.pendente(meuId, targetId);
            }

            FollowRequest fr = FollowRequest.builder()
                    .requesterId(meuId)
                    .targetId(targetId)
                    .status(FollowRequestStatus.PENDENTE)
                    .build();
            followRequestRepository.save(fr);

            log.info("Pedido de follow criado: {} -> {}", meuId, targetId);
            return FollowResponse.pendente(meuId, targetId);
        }

        // Perfil PUBLICO: segue direto
        Follow follow = Follow.builder()
                .followerId(meuId)
                .followeeId(targetId)
                .build();
        followRepository.save(follow);

        log.info("Follow direto criado: {} -> {}", meuId, targetId);
        return FollowResponse.seguindo(meuId, targetId);
    }

    @Transactional
    public FollowResponse deixarDeSeguir(UUID meuId, UUID targetId) {
        if (meuId.equals(targetId)) {
            throw new BusinessException("Operacao invalida", HttpStatus.BAD_REQUEST);
        }

        if (followRepository.existsByFollowerIdAndFolloweeId(meuId, targetId)) {
            followRepository.deleteByFollowerIdAndFolloweeId(meuId, targetId);
            log.info("Unfollow: {} -> {}", meuId, targetId);
        }

        // Cancela pedido pendente se existir
        followRequestRepository
                .findByRequesterIdAndTargetIdAndStatus(meuId, targetId, FollowRequestStatus.PENDENTE)
                .ifPresent(fr -> {
                    fr.setStatus(FollowRequestStatus.CANCELADO);
                    fr.setResolvedAt(OffsetDateTime.now());
                    followRequestRepository.save(fr);
                });

        return FollowResponse.desfeito(meuId, targetId);
    }

    @Transactional(readOnly = true)
    public List<FollowRequestResponse> listarPedidosRecebidos(UUID meuId) {
        return followRequestRepository.findAll().stream()
                .filter(fr -> fr.getTargetId().equals(meuId)
                        && fr.getStatus() == FollowRequestStatus.PENDENTE)
                .map(fr -> {
                    User requester = userRepository.findById(fr.getRequesterId())
                            .orElseThrow(() -> new NotFoundException("Solicitante nao encontrado"));
                    return FollowRequestResponse.from(fr, requester);
                })
                .toList();
    }

    @Transactional
    public FollowResponse aceitarPedido(UUID meuId, UUID requestId) {
        FollowRequest fr = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Pedido nao encontrado"));

        if (!fr.getTargetId().equals(meuId)) {
            throw new BusinessException("Pedido nao pertence a voce", HttpStatus.FORBIDDEN);
        }

        if (fr.getStatus() != FollowRequestStatus.PENDENTE) {
            throw new BusinessException("Pedido ja foi processado", HttpStatus.CONFLICT);
        }

        fr.setStatus(FollowRequestStatus.ACEITO);
        fr.setResolvedAt(OffsetDateTime.now());
        followRequestRepository.save(fr);

        if (!followRepository.existsByFollowerIdAndFolloweeId(fr.getRequesterId(), fr.getTargetId())) {
            Follow follow = Follow.builder()
                    .followerId(fr.getRequesterId())
                    .followeeId(fr.getTargetId())
                    .build();
            followRepository.save(follow);
        }

        log.info("Pedido aceito: {} agora segue {}", fr.getRequesterId(), fr.getTargetId());
        return FollowResponse.seguindo(fr.getRequesterId(), fr.getTargetId());
    }

    @Transactional
    public FollowResponse rejeitarPedido(UUID meuId, UUID requestId) {
        FollowRequest fr = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Pedido nao encontrado"));

        if (!fr.getTargetId().equals(meuId)) {
            throw new BusinessException("Pedido nao pertence a voce", HttpStatus.FORBIDDEN);
        }

        if (fr.getStatus() != FollowRequestStatus.PENDENTE) {
            throw new BusinessException("Pedido ja foi processado", HttpStatus.CONFLICT);
        }

        fr.setStatus(FollowRequestStatus.REJEITADO);
        fr.setResolvedAt(OffsetDateTime.now());
        followRequestRepository.save(fr);

        log.info("Pedido rejeitado: {} -> {}", fr.getRequesterId(), fr.getTargetId());
        return FollowResponse.desfeito(fr.getRequesterId(), fr.getTargetId());
    }
}