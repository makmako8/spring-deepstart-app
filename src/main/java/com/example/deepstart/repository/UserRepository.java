package com.example.deepstart.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deepstart.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
