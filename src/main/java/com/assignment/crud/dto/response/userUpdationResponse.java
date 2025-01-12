package com.assignment.crud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class userUpdationResponse {

    private Integer id;
    private String name;
    private String email;
    private Integer age;
    private String status;
    private String message;
    
}
