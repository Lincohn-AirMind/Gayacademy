package com.gayacademy.user.repository;

import com.gayacademy.user.domain.Follow;
import com.gayacademy.user.domain.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

    void deleteByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

    long countByFolloweeId(UUID followeeId);

    long countByFollowerId(UUID followerId);
}