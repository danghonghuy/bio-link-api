package com.bio.dhh.bio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "content_blocks")
@Data
public class ContentBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Loại block: "link", "header", "youtube", "spotify", ...
    @Column(nullable = false, length = 50)
    private String type;

    // Dữ liệu của block, lưu dưới dạng JSON String. Linh hoạt tối đa.
    @Lob
    @Column(nullable = false, columnDefinition="TEXT")
    private String data;

    @Column(nullable = false)
    private int blockOrder; // Thứ tự hiển thị

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore // Tránh lặp vô hạn khi serialize
    @ToString.Exclude // Tránh lặp vô hạn khi dùng Lombok toString()
    private Profile profile;
}