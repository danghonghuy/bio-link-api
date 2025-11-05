package com.bio.dhh.bio.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data // Tự động tạo getter, setter... của Lombok
@Builder // Giúp tạo đối tượng dễ dàng hơn
public class GitHubStatsDTO {
    private String name;
    private String username;
    private String avatarUrl;
    private int followers;
    private int publicRepos;
    private int totalStars;
    private Map<String, Long> languageStats; // Map để chứa thống kê ngôn ngữ
}