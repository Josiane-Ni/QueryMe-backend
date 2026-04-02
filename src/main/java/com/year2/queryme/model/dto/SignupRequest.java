package com.year2.queryme.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private String role; // TEACHER, STUDENT, ADMIN or GUEST

    @NotBlank
    @Size(min = 3, max = 100)
    private String fullName;

    // Optional for Students
    private Long courseId;
    private Long classGroupId;
}
