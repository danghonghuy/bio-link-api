package com.bio.dhh.bio.repository;

import com.bio.dhh.bio.model.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {

    // Đây là câu query phức tạp: đếm số dòng trong ClickLog,
    // nhóm theo blockId, và chỉ đếm cho những blockId nằm trong danh sách được cung cấp.
    @Query("SELECT c.blockId as blockId, COUNT(c) as count " +
            "FROM ClickLog c " +
            "WHERE c.blockId IN :blockIds " +
            "GROUP BY c.blockId")
    List<ClickCount> countByBlockIdIn(@Param("blockIds") List<Long> blockIds);
}