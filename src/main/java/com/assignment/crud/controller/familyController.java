package com.assignment.crud.controller;

import static com.assignment.crud.utils.myConstant.USER;

import java.util.List;

import static com.assignment.crud.utils.myConstant.FAMILY;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.crud.dto.response.familyResponse;
import com.assignment.crud.service.familyService;

import com.assignment.crud.dto.request.familyRequest;

@RestController
@RequestMapping(USER)
public class familyController {

    private final familyService familyService;

    public familyController(familyService familyService) {
        this.familyService = familyService;
    }

    @PostMapping(FAMILY + "/{id}")
    public ResponseEntity<String> addFamilyMember(@PathVariable Integer id, @RequestBody familyRequest familyRequest) {
        System.out.println("Incoming Request: " + familyRequest);
        System.out.println(familyRequest.getName()+" "+familyRequest.getRelation());
        familyService.addFamilyMember(id, familyRequest);
        return ResponseEntity.ok("Family member added successfully");
    }

    @GetMapping(FAMILY + "/{id}")
    public ResponseEntity<List<familyResponse>> getFamilyMembersByUserId(@PathVariable Integer id) {
        return ResponseEntity.ok(familyService.getFamilyMembersByUserId(id));
    }

    @DeleteMapping(FAMILY + "/{id}" + "/{familyId}")
    public ResponseEntity<String> deleteFamilyMember(@PathVariable Integer id, @PathVariable Integer familyId) {
        familyService.deleteFamilyMember(id, familyId);
        return ResponseEntity.ok("Family member deleted successfully");
    }
    
}
