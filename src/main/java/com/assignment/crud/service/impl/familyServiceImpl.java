package com.assignment.crud.service.impl;

import java.util.List;

import java.util.stream.Collectors;
import com.assignment.crud.exception.ResourceNotFoundException;
import com.assignment.crud.exception.TimeoutException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;

import com.assignment.crud.dto.request.familyRequest;
import com.assignment.crud.dto.response.familyResponse;
import com.assignment.crud.entity.familyEntity;
import com.assignment.crud.entity.userEntity;
import com.assignment.crud.service.familyService;

import com.assignment.crud.repository.*;

import org.slf4j.Logger;

@Service
public class familyServiceImpl implements familyService {

    private static final Logger logger = LoggerFactory.getLogger(familyServiceImpl.class);

    @Autowired
    private familyRepository familyRepository;

    @Autowired
    private userRepository userRepository;

    private familyServiceImpl(familyRepository familyRepository, userRepository userRepository) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
    }

    //inject timeout value
    private int timeoutValue = 5000;


    //add family member
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public void addFamilyMember(Integer userId, familyRequest familyRequestDto) {

        logger.info("Attempting to add family member for user with ID: {}", userId);

        simulate();

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

    @Recover
    public void recover(ResourceNotFoundException e) {
        logger.error("Resource not found exception occurred: {}", e.getMessage());
    }

    @Recover
    public void recover(TimeoutException e) {
        logger.error("Timeout exception occurred: {}", e.getMessage());
    }







    //get family members
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public List<familyResponse> getFamilyMembersByUserId(Integer userId) {

        logger.info("Fetching all family members for user with ID: {}", userId);

        simulate();

        userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });


        logger.info("User found with ID: {}", userId);
        List<familyEntity> familyEntities = familyRepository.findByUserId(userId);
        return familyEntities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Recover
    public List<familyResponse> recoverFromResourceNotFound(ResourceNotFoundException e) {
        logger.error("Resource not found exception occurred: {}", e.getMessage());
        return null;
    }

    @Recover
    public List<familyResponse> recoverFromTimeout(TimeoutException e) {
        logger.error("Timeout exception occurred: {}", e.getMessage());
        return null;
    }






    //delete family member
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public void deleteFamilyMember(Integer id, Integer familyId) {

        logger.info("Attempting to delete family member with ID: {}", familyId);

        simulate();

        userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new RuntimeException("User not found with ID: " + id);
                });

        familyRepository.findById(familyId)
                .orElseThrow(() -> new RuntimeException("Family member not found with ID: " + familyId));

        familyRepository.deleteById(familyId);
    }

    @Recover
    public void recoverFromResourceNotFoundForDelete(ResourceNotFoundException e) {
        logger.error("Resource not found exception occurred: {}", e.getMessage());
    }

    @Recover
    public void recoverFromTimeoutForDelete(TimeoutException e) {
        logger.error("Timeout exception occurred: {}", e.getMessage());
    }







    //map to Response DTO
    private familyResponse mapToResponseDto(familyEntity family) {
        return familyResponse.builder()
                .id(family.getId())
                .name(family.getName())
                .relation(family.getRelation())
                .userId(family.getUser().getId())
                .build();
    }

    public void simulate(){
        // Simulate a delay to test retry mechanism
        try {
            Thread.sleep(timeoutValue);
            logger.info("Thread slept for {} milliseconds", timeoutValue);
            if(timeoutValue > 5000){
                throw new TimeoutException("Timeout occurred while fetching user data");
            }
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while fetching user data");
            Thread.currentThread().interrupt();
        }
    }

    
}
