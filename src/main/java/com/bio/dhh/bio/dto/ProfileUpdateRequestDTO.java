package com.bio.dhh.bio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequestDTO {

    // Giữ userId để xác thực người dùng, nhưng không cho phép thay đổi
    private String userId;

    // Không cần avatarUrl ở đây vì nó được cập nhật riêng
    // private String avatarUrl;

    @NotBlank(message = "Tên hiển thị không được để trống")
    @Size(max = 100, message = "Tên hiển thị không được quá 100 ký tự")
    private String displayName;

    private String description;

    @NotBlank(message = "URL tùy chỉnh không được để trống")
    @Size(max = 100, message = "URL không được quá 100 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "URL chỉ được chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    // Có thể thêm các trường SEO, Giao diện vào đây sau này
    // private String seoTitle;
    // private String theme;
}