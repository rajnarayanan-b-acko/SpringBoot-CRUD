package com.assignment.crud.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.assignment.crud.dto.request.userRequest;
import com.assignment.crud.dto.request.userUpdateRequest;
import com.assignment.crud.dto.response.customMessage;
import com.assignment.crud.dto.response.userCreationResponse;
import com.assignment.crud.dto.response.userResponse;
import com.assignment.crud.dto.response.userUpdationResponse;
import com.assignment.crud.entity.userEntity;
import com.assignment.crud.exception.ResourceNotFoundException;
import com.assignment.crud.repository.userRepository;
import com.assignment.crud.service.userService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class userServiceImpl implements userService {

    private static final Logger logger = LoggerFactory.getLogger(userServiceImpl.class);

    private final userRepository userRepository;

    public userServiceImpl(userRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create a new user
    @Override
    public userCreationResponse createUser(userRequest userRequest) {
        logger.info("Attempting to create a new user with email: {}", userRequest.getEmail());
        userRepository.findByEmail(userRequest.getEmail()).ifPresent(user -> {
            logger.warn("User creation failed: Email {} is already registered", userRequest.getEmail());
            throw new ResourceNotFoundException("User already exists with email : " + userRequest.getEmail());
        });

        userEntity user = new userEntity();
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());
        user.setStatus(userEntity.Status.valueOf(userRequest.getStatus().toUpperCase()));

        userRepository.save(user);
        logger.info("User created successfully with ID: {}", user.getId());

        return userCreationResponse.builder()
                .id(user.getId().toString())
                .message("User created successfully")
                .build();
    }

    // Get all users
    @Override
    public List<userResponse> getAllUsers() {
        logger.info("Fetching all users from the database");
        List<userResponse> users = userRepository.findAll().stream()
                .map(user -> userResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .age(user.getAge())
                        .status(user.getStatus().name())
                        .build())
                .collect(Collectors.toList());
        logger.debug("Retrieved {} users from the database", users.size());
        return users;
    }

    // Get user by ID
    @Override
    public userResponse getUserById(Integer id) {
        logger.info("Fetching user with ID: {}", id);
        userEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", id);
                    return new ResourceNotFoundException("User not found with id : " + id);
                });

        logger.info("User with ID {} found: {}", id, user);
        return userResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .age(user.getAge())
                .status(user.getStatus().name())
                .build();
    }

    // Update user details
    @Override
    public userUpdationResponse updateUser(Integer id, userRequest userRequest) {
        logger.info("Attempting to update user with ID: {}", id);
        userEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot update: User with ID {} not found", id);
                    return new ResourceNotFoundException("User not found with id : " + id);
                });

        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());
        user.setStatus(userEntity.Status.valueOf(userRequest.getStatus().toUpperCase()));

        userRepository.save(user);
        logger.info("User with ID {} updated successfully", id);

        return userUpdationResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .age(user.getAge())
                .status(user.getStatus().name())
                .message("User Details updated successfully")
                .build();
    }

    // Update specific user details
    @Override
    public userUpdationResponse updateSpecificUser(Integer id, userUpdateRequest userRequest) {
        logger.info("Attempting to update specific fields for user with ID: {}", id);
        userEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot update specific fields: User with ID {} not found", id);
                    return new ResourceNotFoundException("User not found with id : " + id);
                });

        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }
        if (userRequest.getAge() != null) {
            user.setAge(userRequest.getAge());
        }
        if (userRequest.getStatus() != null) {
            user.setStatus(userEntity.Status.valueOf(userRequest.getStatus().toUpperCase()));
        }

        userRepository.save(user);
        logger.info("Specific fields for user with ID {} updated successfully", id);

        return userUpdationResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .age(user.getAge())
                .status(user.getStatus().name())
                .message("User Details updated successfully")
                .build();
    }

    // Delete user by ID
    @Override
    public customMessage deleteUser(Integer id) {
        logger.info("Attempting to delete user with ID: {}", id);
        userEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot delete: User with ID {} not found", id);
                    return new ResourceNotFoundException("User not found with id : " + id);
                });

        userRepository.delete(user);
        logger.info("User with ID {} deleted successfully", id);

        return customMessage.builder()
                .message("User deleted successfully")
                .build();
    }
}
