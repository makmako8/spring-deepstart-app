package com.example.deepstart.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deepstart.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByNameContainingIgnoreCase(String keyword);
}
