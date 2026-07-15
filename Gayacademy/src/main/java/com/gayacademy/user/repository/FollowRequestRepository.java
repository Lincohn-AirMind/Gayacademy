package com.gayacademy.user.repository;

import com.gayacademy.user.domain.FollowRequest;
import com.gayacademy.user.domain.enums.FollowRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, UUID> {

    Optional<FollowRequest> findByRequesterIdAndTargetIdAndStatus(
            UUID requesterId, UUID targetId, FollowRequestStatus status);

    boolean existsByRequesterIdAndTargetIdAndStatus(
            UUID requesterId, UUID targetId, FollowRequestStatus status);
}