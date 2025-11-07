package com.bio.dhh.bio.repository;

import com.bio.dhh.bio.model.ClickLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {

    @Query("SELECT cl.blockId as blockId, COUNT(cl.id) as count FROM ClickLog cl WHERE cl.blockId IN :blockIds GROUP BY cl.blockId")
    List<ClickCount> countByBlockIdIn(@Param("blockIds") List<Long> blockIds);

    @Query("SELECT count(cl.id) FROM ClickLog cl JOIN ContentBlock cb ON cl.blockId = cb.id WHERE cb.profile.id = :profileId AND cl.clickedAt BETWEEN :startDate AND :endDate")
    long countTotalClicksByProfileIdInDateRange(@Param("profileId") Long profileId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT CAST(c.clicked_at AS DATE) as date, COUNT(*) as count " +
            "FROM click_logs c JOIN content_blocks cb ON c.block_id = cb.id " +
            "WHERE cb.profile_id = :profileId AND c.clicked_at BETWEEN :startDate AND :endDate " + // <-- THÊM `endDate`
            "GROUP BY CAST(c.clicked_at AS DATE)", nativeQuery = true)
    List<DailyStat> findClickCountsPerDay(@Param("profileId") Long profileId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate); // <-- THÊM `endDate`

    @Query("SELECT cb.id as blockId, COUNT(cl.id) as count, cb.data as blockData " +
            "FROM ClickLog cl JOIN ContentBlock cb ON cl.blockId = cb.id " +
            "WHERE cb.profile.id = :profileId AND cl.clickedAt BETWEEN :startDate AND :endDate " + // <-- THÊM `endDate`
            "GROUP BY cb.id, cb.data " +
            "ORDER BY count DESC")
    List<TopLink> findTopLinksByProfileId(@Param("profileId") Long profileId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}