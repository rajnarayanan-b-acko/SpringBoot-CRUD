package com.assignment.crud.controller;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.assignment.crud.service.userService;
import com.assignment.crud.dto.request.userRequest;
import com.assignment.crud.dto.request.userUpdateRequest;
import com.assignment.crud.dto.response.customMessage;
import com.assignment.crud.dto.response.userCreationResponse;
import com.assignment.crud.dto.response.userResponse;
import com.assignment.crud.dto.response.userUpdationResponse;

import static com.assignment.crud.utils.myConstant.USER;
import static com.assignment.crud.utils.myConstant.CREATE;
import static com.assignment.crud.utils.myConstant.GET_ALL;
import static com.assignment.crud.utils.myConstant.GET_BY_ID;
import static com.assignment.crud.utils.myConstant.UPDATE_BY_ID;
import static com.assignment.crud.utils.myConstant.DELETE_BY_ID;
import static com.assignment.crud.utils.myConstant.PATCH_BY_ID;

@RestController
@RequestMapping(USER)
@RequiredArgsConstructor
public class userController {

    
    private final userService userService;

   
    @PostMapping(CREATE)
    public ResponseEntity<userCreationResponse> createUser(@RequestBody userRequest userRequest) {
        return ResponseEntity.ok(userService.createUser(userRequest));
    }

   
    @GetMapping(GET_ALL)
    public ResponseEntity<List<userResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    
    @GetMapping(GET_BY_ID + "/{id}")
    public ResponseEntity<userResponse> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    
    @PutMapping(UPDATE_BY_ID + "/{id}")
    public ResponseEntity<userUpdationResponse> updateUser(@PathVariable Integer id, @RequestBody userRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

  
    @PatchMapping(PATCH_BY_ID + "/{id}")
    public ResponseEntity<userUpdationResponse> updatespecificUser(@PathVariable Integer id, @RequestBody userUpdateRequest userRequest) {
        return ResponseEntity.ok(userService.updateSpecificUser(id, userRequest));
    }


    @DeleteMapping(DELETE_BY_ID + "/{id}")
    public ResponseEntity<customMessage> deleteUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
    

    
}
