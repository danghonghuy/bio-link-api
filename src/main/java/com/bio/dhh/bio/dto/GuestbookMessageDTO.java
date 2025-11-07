// file: com/bio/dhh/bio/dto/GuestbookMessageDTO.java
package com.bio.dhh.bio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GuestbookMessageDTO {
    // Dùng để gửi từ client lên server
    @NotBlank
    @Size(max = 100)
    private String authorName;

    @NotBlank
    @Size(max = 2000)
    private String messageContent;

    @NotNull
    private Boolean isPublic;

    // Dùng để gửi từ server về client
    private Long id;
    private LocalDateTime createdAt;
}