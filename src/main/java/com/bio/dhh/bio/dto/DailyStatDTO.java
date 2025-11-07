package com.bio.dhh.bio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor // Cần cho Jackson
@AllArgsConstructor // Cần cho chúng ta tạo đối tượng
public class DailyStatDTO {
    private LocalDate date; // <-- QUAN TRỌNG: Đổi sang LocalDate
    private int views;
    private int clicks;
}