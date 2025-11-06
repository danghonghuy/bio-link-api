package com.bio.dhh.bio.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profiles")
@Data
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Lob
    @Column(columnDefinition="TEXT")
    private String description;

    @Column(length = 1000)
    private String avatarUrl;

    @Column(length = 255)
    private String background;

    private Integer backgroundImageOpacity;

    // === CÀI ĐẶT GIAO DIỆN MỚI ===
    @Column(length = 50)
    private String buttonStyle; // e.g., 'rounded-full', 'rounded-lg', 'rounded-none'

    @Column(length = 50)
    private String font; // e.g., 'font-inter', 'font-roboto-mono'

    // === CÀI ĐẶT SEO MỚI ===
    @Column(length = 120)
    private String seoTitle;

    @Column(length = 255)
    private String seoDescription;

    @Column(length = 1000)
    private String socialImage;


    @Column(nullable = false, unique = true)
    private String userId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("blockOrder ASC")
    private List<ContentBlock> blocks = new ArrayList<>();
}