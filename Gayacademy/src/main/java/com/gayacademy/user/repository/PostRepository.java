package com.gayacademy.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gayacademy.user.domain.PostActions.Post;

import java.util.UUID;

public interface PostRepository 
extends JpaRepository<Post, UUID> {
}