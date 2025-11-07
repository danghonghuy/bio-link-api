package com.bio.dhh.bio.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column(nullable = false, length = 50)
    private String type;

    @Lob
    @Column(nullable = false, columnDefinition="TEXT")
    private String data;

    @Column(nullable = false)
    private int blockOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private Profile profile;
}