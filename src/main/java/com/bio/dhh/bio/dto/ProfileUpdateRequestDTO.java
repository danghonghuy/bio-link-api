package com.bio.dhh.bio.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequestDTO {

    private String userId;

    @NotBlank(message = "Tên hiển thị không được để trống")
    @Size(max = 100, message = "Tên hiển thị không được quá 100 ký tự")
    private String displayName;

    private String description;

    @NotBlank(message = "URL tùy chỉnh không được để trống")
    @Size(max = 100, message = "URL không được quá 100 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "URL chỉ được chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    @Size(max = 255, message = "Lựa chọn background không hợp lệ")
    private String background;
    @Size(max = 1000, message = "URL ảnh đại diện quá dài")
    private String avatarUrl;
    @Min(value = 0, message = "Độ mờ tối thiểu là 0")
    @Max(value = 100, message = "Độ mờ tối đa là 100")
    private Integer backgroundImageOpacity;
    @Size(max = 50, message = "Google Analytics ID không hợp lệ")
    private String googleAnalyticsId;
    @Size(max = 50, message = "Mã màu không hợp lệ")
    private String fontColor;
    @Size(max = 50, message = "Facebook Pixel ID không hợp lệ")
    private String facebookPixelId;
    // ▼▼▼ DÁN 5 DÒNG NÀY VÀO ▼▼▼
    @Size(max = 50)
    private String buttonStyle;

    @Size(max = 50)
    private String font;

    @Size(max = 120)
    private String seoTitle;

    @Size(max = 255)
    private String seoDescription;

    @Size(max = 1000)
    private String socialImage;
}