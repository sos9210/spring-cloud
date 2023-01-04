package com.example.userservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,String> {
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findByEmail(String email);
}
