package com.assignment.crud.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class userRequest {

    @NotNull
    private String name;

    @Email
    @NotNull
    private String email;

    @NotNull
    private Integer age;

    @NotNull
    private String status;

}
