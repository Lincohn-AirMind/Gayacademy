package com.gayacademy.user.repository;

import com.gayacademy.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("""
           SELECT u FROM User u
           WHERE LOWER(u.email) = LOWER(:identificador)
              OR LOWER(u.username) = LOWER(:identificador)
           """)
    Optional<User> findByEmailOrUsername(@Param("identificador") String identificador);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}