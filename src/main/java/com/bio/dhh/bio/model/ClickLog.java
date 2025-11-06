package com.bio.dhh.bio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_logs")
@Data
public class ClickLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lưu lại ID của block (link) nào đã được click
    private Long blockId;

    // Tự động ghi lại thời gian click
    @CreationTimestamp
    private LocalDateTime clickedAt;
}