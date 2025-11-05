package com.bio.dhh.bio.service;

import com.bio.dhh.bio.dto.GitHubStatsDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GitHubService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GitHubStatsDTO getGitHubStats(String username) throws IOException {
        String userApiUrl = "https://api.github.com/users/" + username;
        String reposApiUrl = "https://api.github.com/users/" + username + "/repos?per_page=100";

        String userJsonResponse = restTemplate.getForObject(userApiUrl, String.class);
        JsonNode userRoot = objectMapper.readTree(userJsonResponse);

        String reposJsonResponse = restTemplate.getForObject(reposApiUrl, String.class);
        JsonNode reposArray = objectMapper.readTree(reposJsonResponse);

        int totalStars = 0;
        Map<String, Long> languageCounts = new HashMap<>();
        long totalLanguageVotes = 0; // Đổi tên để rõ ràng hơn

        if (reposArray.isArray()) {
            for (JsonNode repo : reposArray) {
                totalStars += repo.get("stargazers_count").asInt();
                String lang = repo.get("language").asText("null");
                if (!"null".equals(lang)) {
                    languageCounts.put(lang, languageCounts.getOrDefault(lang, 0L) + 1);
                    totalLanguageVotes++;
                }
            }
        }

        // Tính toán % - PHIÊN BẢN ĐÃ SỬA LỖI
        Map<String, Long> languagePercentage = new HashMap<>();
        if (totalLanguageVotes > 0) {
            for (Map.Entry<String, Long> entry : languageCounts.entrySet()) {
                String lang = entry.getKey();
                Long count = entry.getValue();
                long percentage = Math.round(((double) count / totalLanguageVotes) * 100);
                if (percentage > 0) {
                    languagePercentage.put(lang, percentage);
                }
            }
        }

        return GitHubStatsDTO.builder()
                .name(userRoot.get("name").asText("No Name"))
                .username(userRoot.get("login").asText())
                .avatarUrl(userRoot.get("avatar_url").asText())
                .followers(userRoot.get("followers").asInt())
                .publicRepos(userRoot.get("public_repos").asInt())
                .totalStars(totalStars)
                .languageStats(languagePercentage)
                .build();
    }
}