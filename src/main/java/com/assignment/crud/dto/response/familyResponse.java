package com.assignment.crud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class familyResponse {

    private Integer id;
    private String name;
    private String relation;
    private Integer userId;
    
}
