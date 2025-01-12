package com.assignment.crud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.assignment.crud.entity.userEntity;

public interface userRepository extends JpaRepository<userEntity, Integer> {

    @Query("SELECT u FROM userEntity u WHERE u.email = :email")
    Optional<userEntity> findByEmail(String email);
    
}
