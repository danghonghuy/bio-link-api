package com.bio.dhh.bio.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnalyticsDTO {
    private long totalViews;
    private long totalClicks;
    private List<DailyStatDTO> dailyStats;
    private List<TopLinkDTO> topLinks;
}