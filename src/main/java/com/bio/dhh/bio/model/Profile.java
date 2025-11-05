package com.bio.dhh.bio.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity                 // Báo cho JPA biết đây là một "Tờ Giấy Mẫu" tương ứng với một bảng DB
@Table(name = "profiles") // Chỉ rõ tên bảng là "profiles"
@Data                   // Lombok: Tự tạo getter, setter...
public class Profile {

    @Id                                             // Đánh dấu đây là khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Báo rằng DB sẽ tự động tăng giá trị này
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String avatarUrl;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String facebookLink;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String youtubeLink;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String tiktokLink;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String discordLink;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String githubLink;

    @CreationTimestamp // Tự động điền ngày giờ tạo
    @Column(updatable = false)
    private LocalDateTime createdAt;
}