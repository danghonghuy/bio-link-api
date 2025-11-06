package com.bio.dhh.bio.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Data
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(nullable = false, length = 100)
    private String displayName;

    // SỬA: Dùng @Lob để tạo kiểu TEXT cho nội dung dài
    @Lob
    private String description;

    // SỬA: Dùng VARCHAR với độ dài hợp lý cho các đường link và URL
    @Column(length = 1000)
    private String avatarUrl;

    @Column(length = 500)
    private String facebookLink;

    @Column(length = 500)
    private String youtubeLink;

    @Column(length = 500)
    private String tiktokLink;

    @Column(length = 500)
    private String discordLink;

    @Column(length = 500)
    private String githubLink;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}