package com.bio.dhh.bio.repository;

import com.bio.dhh.bio.model.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {

    long countByProfileId(Long profileId);

    @Query(value = "SELECT CAST(v.viewed_at AS DATE) as date, COUNT(*) as count " +
            "FROM view_logs v " +
            "WHERE v.profile_id = :profileId AND v.viewed_at >= :startDate " +
            "GROUP BY CAST(v.viewed_at AS DATE)", nativeQuery = true)
    List<DailyStat> findViewCountsPerDay(@Param("profileId") Long profileId, @Param("startDate") LocalDateTime startDate);
}