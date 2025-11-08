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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Transactional
    public Profile recordProfileView(String slug) {
        Profile profile = profileRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile không tồn tại"));

        ViewLog viewLog = new ViewLog();
        viewLog.setProfile(profile);
        viewLogRepository.save(viewLog);

        profileRepository.incrementViewsBySlug(slug);

        return profileRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tải lại profile sau khi cập nhật view"));
    }
    @Transactional(readOnly = true)
    public Optional<Profile> getMyProfile(String userId) {
        return profileRepository.findByUserIdWithBlocks(userId);
    }
    public AnalyticsDTO getAnalytics(String userId, String range) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy profile"));
        long profileId = profile.getId();

        // 1. Xác định khoảng thời gian start và end date
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate = now;
        int daysInRange;

        switch (range) {
            case "today":
                startDate = now.toLocalDate().atStartOfDay();
                daysInRange = 1;
                break;
            case "yesterday":
                startDate = now.toLocalDate().minusDays(1).atStartOfDay();
                endDate = now.toLocalDate().atStartOfDay().minusNanos(1);
                daysInRange = 1;
                break;
            case "30d":
                startDate = now.minusDays(29).toLocalDate().atStartOfDay();
                daysInRange = 30;
                break;
            case "all":
                startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
                daysInRange = -1; // Cờ hiệu để không điền ngày trống
                break;
            case "7d":
            default:
                startDate = now.minusDays(6).toLocalDate().atStartOfDay();
                daysInRange = 7;
                break;
        }

        // 2. Gọi các repository đã được sửa đổi
        AnalyticsDTO analyticsDTO = new AnalyticsDTO();
        analyticsDTO.setTotalViews(viewLogRepository.countByProfileIdInDateRange(profileId, startDate, endDate));
        analyticsDTO.setTotalClicks(clickLogRepository.countTotalClicksByProfileIdInDateRange(profileId, startDate, endDate));

        List<DailyStat> dailyViews = viewLogRepository.findViewCountsPerDay(profileId, startDate, endDate);
        List<DailyStat> dailyClicks = clickLogRepository.findClickCountsPerDay(profileId, startDate, endDate);

        // 3. Kết hợp và điền các ngày còn thiếu
        Map<LocalDate, DailyStatDTO> combinedStats = new TreeMap<>();

        // Điền các ngày trống với giá trị 0 (trừ trường hợp "all time")
        if (daysInRange > 0) {
            LocalDate initialDate = startDate.toLocalDate();
            IntStream.range(0, daysInRange).forEach(i -> {
                LocalDate date = initialDate.plusDays(i);
                combinedStats.put(date, new DailyStatDTO(date, 0, 0));
            });
        }

        dailyViews.forEach(v -> combinedStats.computeIfAbsent(v.getDate().toLocalDate(), k -> new DailyStatDTO(k, 0, 0)).setViews(v.getCount()));
        dailyClicks.forEach(c -> combinedStats.computeIfAbsent(c.getDate().toLocalDate(), k -> new DailyStatDTO(k, 0, 0)).setClicks(c.getCount()));

        // 4. KHÔNG ĐỊNH DẠNG NGÀY THÁNG, để FE tự làm
        analyticsDTO.setDailyStats(combinedStats.values().stream().collect(Collectors.toList()));

        // 5. Lấy Top Links trong khoảng thời gian
        List<TopLink> topLinks = clickLogRepository.findTopLinksByProfileId(profileId, startDate, endDate, PageRequest.of(0, 5));
        analyticsDTO.setTopLinks(topLinks.stream().map(this::mapToTopLinkDTO).collect(Collectors.toList()));

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