package com.assignment.crud.service;

import java.util.List;

import com.assignment.crud.dto.request.familyRequest;
import com.assignment.crud.dto.response.familyResponse;

public interface familyService {

    void addFamilyMember(Integer userId, familyRequest familyRequestDto);

    List<familyResponse> getFamilyMembersByUserId(Integer userId);

    void deleteFamilyMember(Integer id, Integer familyId);
    
}
