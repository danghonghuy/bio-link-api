package com.bio.dhh.bio.service;

import com.bio.dhh.bio.dto.AnalyticsDTO;
import com.bio.dhh.bio.dto.DailyStatDTO;
import com.bio.dhh.bio.dto.TopLinkDTO;
import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.model.ViewLog;
import com.bio.dhh.bio.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ViewLogRepository viewLogRepository;
    private final ClickLogRepository clickLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ProfileService(ProfileRepository profileRepository, ViewLogRepository viewLogRepository, ClickLogRepository clickLogRepository) {
        this.profileRepository = profileRepository;
        this.viewLogRepository = viewLogRepository;
        this.clickLogRepository = clickLogRepository;
    }

    public void recordProfileView(String slug) {
        Profile profile = profileRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ViewLog viewLog = new ViewLog();
        viewLog.setProfile(profile);
        viewLogRepository.save(viewLog);
    }

    public AnalyticsDTO getAnalytics(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy profile"));

        AnalyticsDTO analyticsDTO = new AnalyticsDTO();
        long profileId = profile.getId();

        // 1. Get totals
        analyticsDTO.setTotalViews(viewLogRepository.countByProfileId(profileId));
        analyticsDTO.setTotalClicks(clickLogRepository.countTotalClicksByProfileId(profileId));

        // 2. Get daily stats for last 7 days
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
        List<DailyStat> dailyViews = viewLogRepository.findViewCountsPerDay(profileId, sevenDaysAgo);
        List<DailyStat> dailyClicks = clickLogRepository.findClickCountsPerDay(profileId, sevenDaysAgo);

        Map<LocalDate, Integer> viewsMap = dailyViews.stream()
                .collect(Collectors.toMap(stat -> stat.getDate().toLocalDate(), DailyStat::getCount));
        Map<LocalDate, Integer> clicksMap = dailyClicks.stream()
                .collect(Collectors.toMap(stat -> stat.getDate().toLocalDate(), DailyStat::getCount));

        List<DailyStatDTO> dailyStats = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            DailyStatDTO statDTO = new DailyStatDTO();

            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("vi", "VN"));
            if(date.isEqual(LocalDate.now())) {
                dayName = "Hôm nay";
            } else if (date.isEqual(LocalDate.now().minusDays(1))) {
                dayName = "Hôm qua";
            }

            statDTO.setDate(dayName);
            statDTO.setViews(viewsMap.getOrDefault(date, 0));
            statDTO.setClicks(clicksMap.getOrDefault(date, 0));
            dailyStats.add(statDTO);
        }
        analyticsDTO.setDailyStats(dailyStats);

        // 3. Get top 5 links
        List<TopLink> topLinks = clickLogRepository.findTopLinksByProfileId(profileId, PageRequest.of(0, 5));
        List<TopLinkDTO> topLinkDTOs = topLinks.stream().map(this::mapToTopLinkDTO).collect(Collectors.toList());
        analyticsDTO.setTopLinks(topLinkDTOs);

        return analyticsDTO;
    }

    private TopLinkDTO mapToTopLinkDTO(TopLink topLink) {
        TopLinkDTO dto = new TopLinkDTO();
        dto.setBlockId(topLink.getBlockId());
        dto.setCount(topLink.getCount());

        try {
            JsonNode root = objectMapper.readTree(topLink.getBlockData());
            dto.setTitle(root.has("title") ? root.get("title").asText() : null);
            dto.setUrl(root.has("url") ? root.get("url").asText() : null);
        } catch (JsonProcessingException e) {
            dto.setTitle("Dữ liệu lỗi");
        }

        return dto;
    }
}