// Trong file: com/bio/dhh/bio/dto/AuthorCommentDTO.java

package com.bio.dhh.bio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorCommentDTO {
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    private String messageContent;
}