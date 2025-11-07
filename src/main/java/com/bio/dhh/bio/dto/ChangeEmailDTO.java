package com.bio.dhh.bio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailDTO {
    @NotBlank
    private String userId;

    @NotBlank
    @Email
    private String newEmail;

    @NotBlank
    private String password;
}