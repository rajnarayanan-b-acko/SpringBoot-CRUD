package com.assignment.crud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.assignment.crud.entity.familyEntity;

public interface familyRepository extends JpaRepository<familyEntity, Integer> {

    List<familyEntity> findByUserId(Integer userId);
    void deleteByIdAndUserId(Integer familyId, Integer userId);

    
}
