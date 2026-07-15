package com.gayacademy.user.dto;

import com.gayacademy.user.domain.FollowRequest;
import com.gayacademy.user.domain.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FollowRequestResponse(
        UUID requestId,
        UUID requesterId,
        String requesterUsername,
        String requesterNomeExibicao,
        String status,
        OffsetDateTime createdAt
) {
    public static FollowRequestResponse from(FollowRequest fr, User requester) {
        return new FollowRequestResponse(
                fr.getId(),
                fr.getRequesterId(),
                requester.getUsername(),
                requester.getNomeExibicao(),
                fr.getStatus().name(),
                fr.getCreatedAt()
        );
    }
}