package com.bio.dhh.bio.dto;

import lombok.Data;

@Data
public class TopLinkDTO {
    private Long blockId;
    private String title;
    private String url;
    private long count;
}