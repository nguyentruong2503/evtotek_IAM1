package com.example.iam1.repository;

import com.example.iam1.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndActive(String email, boolean active);

    Optional<UserEntity> findById(Long id);

    UserEntity findByEmail(String email);
    boolean existsByEmail(String email);
}
