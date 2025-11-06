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

    @Column(nullable = false, unique = true)
    private String userId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ▼▼▼ THÊM QUAN HỆ MỚI ▼▼▼
    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER // Tải các block cùng lúc với profile
    )
    @OrderBy("blockOrder ASC") // Luôn trả về danh sách đã sắp xếp
    private List<ContentBlock> blocks = new ArrayList<>();
}