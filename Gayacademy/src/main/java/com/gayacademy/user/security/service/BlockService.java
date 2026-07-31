package com.gayacademy.user.security.service;

import com.gayacademy.user.domain.Block;
import com.gayacademy.user.dto.BlockResponse;
import com.gayacademy.user.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;

    @Transactional
    public BlockResponse bloquear(UUID blockerId, UUID blockedId) {
        if (!blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            blockRepository.save(Block.builder()
                    .blockerId(blockerId)
                    .blockedId(blockedId)
                    .build());
        }
        return BlockResponse.bloqueado(blockerId, blockedId);
    }

    @Transactional
    public BlockResponse desbloquear(UUID blockerId, UUID blockedId) {
        blockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);
        return BlockResponse.desbloqueado(blockerId, blockedId);
    }
}
