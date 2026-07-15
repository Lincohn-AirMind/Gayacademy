package com.gayacademy.user;

import com.gayacademy.common.exception.BusinessException;
import com.gayacademy.common.exception.NotFoundException;
import com.gayacademy.user.domain.Block;
import com.gayacademy.user.dto.BlockResponse;
import com.gayacademy.user.repository.BlockRepository;
import com.gayacademy.user.repository.FollowRepository;
import com.gayacademy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockService {

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;

    @Transactional
    public BlockResponse bloquear(UUID meuId, UUID targetId) {
        if (meuId.equals(targetId)) {
            throw new BusinessException("Voce nao pode bloquear a si mesmo", HttpStatus.BAD_REQUEST);
        }

        if (!userRepository.existsById(targetId)) {
            throw new NotFoundException("Usuario nao encontrado");
        }

        // Idempotencia: ja bloqueado?
        if (blockRepository.existsByBlockerIdAndBlockedId(meuId, targetId)) {
            return BlockResponse.bloqueado(meuId, targetId);
        }

        Block block = Block.builder()
                .blockerId(meuId)
                .blockedId(targetId)
                .build();
        blockRepository.save(block);

        // Remove follow nos dois sentidos
        if (followRepository.existsByFollowerIdAndFolloweeId(meuId, targetId)) {
            followRepository.deleteByFollowerIdAndFolloweeId(meuId, targetId);
        }
        if (followRepository.existsByFollowerIdAndFolloweeId(targetId, meuId)) {
            followRepository.deleteByFollowerIdAndFolloweeId(targetId, meuId);
        }

        log.info("Bloqueio criado: {} bloqueou {}", meuId, targetId);
        return BlockResponse.bloqueado(meuId, targetId);
    }

    @Transactional
    public BlockResponse desbloquear(UUID meuId, UUID targetId) {
        if (meuId.equals(targetId)) {
            throw new BusinessException("Operacao invalida", HttpStatus.BAD_REQUEST);
        }

        if (blockRepository.existsByBlockerIdAndBlockedId(meuId, targetId)) {
            blockRepository.deleteByBlockerIdAndBlockedId(meuId, targetId);
            log.info("Desbloqueio: {} -> {}", meuId, targetId);
        }

        return BlockResponse.desbloqueado(meuId, targetId);
    }
}