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

    @Query("SELECT b.id as blockId, COUNT(c.id) as count FROM ClickLog c JOIN c.contentBlock b WHERE b.id IN :blockIds GROUP BY b.id")
    List<ClickCount> countByBlockIdIn(@Param("blockIds") List<Long> blockIds);

    @Query("SELECT count(c.id) FROM ClickLog c WHERE c.contentBlock.profile.id = :profileId")
    long countTotalClicksByProfileId(@Param("profileId") Long profileId);

    @Query(value = "SELECT CAST(c.clicked_at AS DATE) as date, COUNT(*) as count " +
            "FROM click_logs c JOIN content_blocks cb ON c.content_block_id = cb.id " +
            "WHERE cb.profile_id = :profileId AND c.clicked_at >= :startDate " +
            "GROUP BY CAST(c.clicked_at AS DATE)", nativeQuery = true)
    List<DailyStat> findClickCountsPerDay(@Param("profileId") Long profileId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT cb.id as blockId, COUNT(cl.id) as count, cb.data as blockData " +
            "FROM ClickLog cl JOIN cl.contentBlock cb " +
            "WHERE cb.profile.id = :profileId " +
            "GROUP BY cb.id, cb.data " +
            "ORDER BY count DESC")
    List<TopLink> findTopLinksByProfileId(@Param("profileId") Long profileId, Pageable pageable);
}