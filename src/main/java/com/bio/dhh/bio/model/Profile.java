package com.bio.dhh.bio.model;

import jakarta.persistence.*;
import lombok.Data; // Giữ nguyên Lombok nếu bạn dùng, không cần thêm getter/setter thủ công
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Data
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- CÁC TRƯỜNG DỮ LIỆU CỦA BIO ---
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Lob
    @Column(columnDefinition="TEXT") // Định nghĩa rõ hơn cho cross-database
    private String description;

    @Column(length = 1000)
    private String avatarUrl;

    // --- CÁC TRƯỜNG LINK MXH ---
    @Column(length = 500)
    private String facebookLink;

    @Column(length = 500)
    private String youtubeLink;

    @Column(length = 500)
    private String tiktokLink;

    @Column(length = 500)
    private String githubLink;

    // ▼▼▼ TRƯỜNG QUAN TRỌNG CÒN THIẾU ĐÂY ▼▼▼
    @Column(nullable = false, unique = true) // Mỗi người dùng chỉ có 1 profile
    private String userId; // Sẽ lưu ID từ Firebase, ví dụ: "XVc8a...3s2"
    @Column(length = 50, nullable = false, columnDefinition = "varchar(50) default 'default'")
    private String theme;
    // --- CÁC TRƯỜNG METADATA ---
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Lưu ý: Nhờ có @Data (Lombok), bạn không cần viết thủ công getter/setter
    // cho 'userId' nữa. Lombok sẽ tự làm điều đó.
}