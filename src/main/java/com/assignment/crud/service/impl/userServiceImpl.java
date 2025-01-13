package com.assignment.crud.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.assignment.crud.dto.request.userRequest;
import com.assignment.crud.dto.request.userUpdateRequest;
import com.assignment.crud.dto.response.customMessage;
import com.assignment.crud.dto.response.userCreationResponse;
import com.assignment.crud.dto.response.userResponse;
import com.assignment.crud.dto.response.userUpdationResponse;
import com.assignment.crud.entity.userEntity;
import com.assignment.crud.exception.ResourceNotFoundException;
import com.assignment.crud.exception.TimeoutException;
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

    //inject timeout value
    private int timeoutValue = 5000;
    


    // Create a new user
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public userCreationResponse createUser(userRequest userRequest) {

        logger.info("Attempting to create a new user with email: {}", userRequest.getEmail());

        simulate();

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

    @Recover
    public userCreationResponse recover(ResourceNotFoundException e, userRequest userRequest) {
        logger.error("User creation failed after 3 attempts: {}", e.getMessage());
        return userCreationResponse.builder()
                .id(null)
                .message("User creation failed after 3 attempts")
                .build();
    }

    @Recover
    public userCreationResponse recover(TimeoutException e, userRequest userRequest) {
        logger.error("User creation failed after 3 attempts due to timeout: {}", e.getMessage());
        return userCreationResponse.builder()
                .id(null)
                .message("User creation failed after 3 attempts due to timeout")
                .build();
    }



    

    // Get all users
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public List<userResponse> getAllUsers() {

        logger.info("Fetching all users from the database");

        simulate();

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

    @Recover
    public List<userResponse> recover(ResourceNotFoundException e) {
        logger.error("Fetching all users failed after 3 attempts: {}", e.getMessage());
        return null;
    }







    // Get user by ID
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class}, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public userResponse getUserById(Integer id) {

        logger.info("Fetching user with ID: {}", id);

        simulate();

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

    @Recover
    public userResponse recover(ResourceNotFoundException e, Integer id) {
        logger.error("Fetching user with ID {} failed after 3 attempts: {}", id, e.getMessage());
        return userResponse.builder()
                .id(id)
                .email(null)
                .name(null)
                .age(null)
                .status(null)
                .build();
    }

    @Recover
    public userResponse recover(TimeoutException e, Integer id) {
        logger.error("Fetching user with ID {} failed after 3 attempts due to timeout: {}", id, e.getMessage());
        return userResponse.builder()
                .id(id)
                .email(null)
                .name(null)
                .age(null)
                .status(null)
                .build();
    }







    // Update user details
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class}, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public userUpdationResponse updateUser(Integer id, userRequest userRequest) {

        logger.info("Attempting to update user with ID: {}", id);

        simulate();

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

    @Recover
    public userUpdationResponse recover(ResourceNotFoundException e, Integer id, userRequest userRequest) {
        logger.error("User update failed after 3 attempts: {}", e.getMessage());
        return userUpdationResponse.builder()
                .id(id)
                .email(null)
                .name(null)
                .age(null)
                .status(null)
                .message("User update failed after 3 attempts")
                .build();
    }

    @Recover
    public userUpdationResponse recover(TimeoutException e, Integer id, userRequest userRequest) {
        logger.error("User update failed after 3 attempts due to timeout: {}", e.getMessage());
        return userUpdationResponse.builder()
                .id(id)
                .email(null)
                .name(null)
                .age(null)
                .status(null)
                .message("User update failed after 3 attempts due to timeout")
                .build();
    }




    

    // Update specific user details
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class}, 
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public userUpdationResponse updateSpecificUser(Integer id, userUpdateRequest userRequest) {

        logger.info("Attempting to update specific fields for user with ID: {}", id);

        simulate();

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

    @Recover
    public userUpdationResponse recover(ResourceNotFoundException e, Integer id, userUpdateRequest userRequest) {
        logger.error("User update failed after 3 attempts: {}", e.getMessage());
        return userUpdationResponse.builder()
                .id(null)
                .email(null)
                .name(null)
                .age(null)
                .status(null)
                .message("User update failed after 3 attempts")
                .build();
    }

    @Recover
    public userUpdationResponse recover(TimeoutException e, Integer id, userUpdateRequest userRequest) {
        logger.error("User update failed after 3 attempts due to timeout: {}", e.getMessage());
        return userUpdationResponse.builder()
                .id(null)
                .email(null)
                .name(null)
                .age(null)
                .status(null)
                .message("User update failed after 3 attempts due to timeout")
                .build();
    }






    // Delete user by ID
    @Retryable(
        value = {ResourceNotFoundException.class, TimeoutException.class}, 
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public customMessage deleteUser(Integer id) {

        logger.info("Attempting to delete user with ID: {}", id);

        simulate();

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

    @Recover
    public customMessage recoverDelete(ResourceNotFoundException e, Integer id) {
        logger.error("User deletion failed after 3 attempts: {}", e.getMessage());
        return customMessage.builder()
                .message("User deletion failed after 3 attempts")
                .build();
    }

    @Recover
    public customMessage recoverDelete(TimeoutException e, Integer id) {
        logger.error("User deletion failed after 3 attempts due to timeout: {}", e.getMessage());
        return customMessage.builder()
                .message("User deletion failed after 3 attempts due to timeout")
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
