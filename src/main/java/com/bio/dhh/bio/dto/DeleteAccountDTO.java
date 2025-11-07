package com.bio.dhh.bio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountDTO {
    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}