package com.assignment.crud.service;

import java.util.List;

import com.assignment.crud.dto.request.userRequest;
import com.assignment.crud.dto.request.userUpdateRequest;
import com.assignment.crud.dto.response.customMessage;
import com.assignment.crud.dto.response.userCreationResponse;
import com.assignment.crud.dto.response.userResponse;
import com.assignment.crud.dto.response.userUpdationResponse;

public interface userService {

    userCreationResponse createUser(userRequest userRequest);

    List<userResponse> getAllUsers();

    userResponse getUserById(Integer id);

    userUpdationResponse updateUser(Integer id, userRequest userRequest);

    userUpdationResponse updateSpecificUser(Integer id, userUpdateRequest userRequest);

    customMessage deleteUser(Integer id);
    
}
