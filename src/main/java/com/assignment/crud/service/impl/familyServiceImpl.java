package com.assignment.crud.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignment.crud.dto.request.familyRequest;
import com.assignment.crud.dto.response.familyResponse;
import com.assignment.crud.entity.familyEntity;
import com.assignment.crud.entity.userEntity;
import com.assignment.crud.service.familyService;

import com.assignment.crud.repository.*;

import org.slf4j.Logger;

@Service
public class familyServiceImpl implements familyService {

    @Autowired
    private familyRepository familyRepository;

    @Autowired
    private userRepository userRepository;

    private familyServiceImpl(familyRepository familyRepository, userRepository userRepository) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(familyServiceImpl.class);

    @Override
    public void addFamilyMember(Integer userId, familyRequest familyRequestDto) {

        logger.info("Attempting to add family member for user with ID: {}", userId);
        userEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });

        logger.info("Received family request: Name={}, Relation={}", familyRequestDto.getName(), familyRequestDto.getRelation());

        familyEntity family = familyEntity.builder()
                .name(familyRequestDto.getName())
                .relation(familyRequestDto.getRelation())
                .user(user)
                .build();

        logger.info("Family member added successfully for user with ID: {}", userId);
        familyRepository.save(family);
    }

    @Override
    public List<familyResponse> getFamilyMembersByUserId(Integer userId) {
        logger.info("Fetching all family members for user with ID: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });


        logger.info("User found with ID: {}", userId);
        List<familyEntity> familyEntities = familyRepository.findByUserId(userId);
        return familyEntities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public void deleteFamilyMember(Integer id, Integer familyId) {
        logger.info("Attempting to delete family member with ID: {}", familyId);
        userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new RuntimeException("User not found with ID: " + id);
                });

        familyRepository.findById(familyId)
                .orElseThrow(() -> new RuntimeException("Family member not found with ID: " + familyId));

        familyRepository.deleteById(familyId);
    }

    private familyResponse mapToResponseDto(familyEntity family) {
        return familyResponse.builder()
                .id(family.getId())
                .name(family.getName())
                .relation(family.getRelation())
                .userId(family.getUser().getId())
                .build();
    }

    
}
