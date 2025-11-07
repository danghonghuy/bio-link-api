// file: com/bio/dhh/bio/model/GuestbookMessage.java
package com.bio.dhh.bio.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "guestbook_messages")
@Data
public class GuestbookMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    // --- THÊM DÒNG NÀY ---
    @Column(nullable = false)
    private Boolean isRead = false; // Mặc định là chưa đọc
    @Column(nullable = false)
    private Boolean isAuthor = false; // Mặc định là khách
    @Column(nullable = false, length = 100)
    private String authorName;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String messageContent;

    @Column(nullable = false)
    private Boolean isPublic = false; // Mặc định là riêng tư

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}