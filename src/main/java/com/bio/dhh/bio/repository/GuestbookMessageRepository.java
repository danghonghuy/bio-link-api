// file: com/bio/dhh/bio/repository/GuestbookMessageRepository.java
package com.bio.dhh.bio.repository;

import com.bio.dhh.bio.model.GuestbookMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuestbookMessageRepository extends JpaRepository<GuestbookMessage, Long> {
    List<GuestbookMessage> findByProfileIdAndIsPublicTrueOrderByCreatedAtDesc(Long profileId);
    List<GuestbookMessage> findByProfileIdOrderByCreatedAtDesc(Long profileId);
    // Đếm số tin nhắn chưa đọc của một profile
    long countByProfileIdAndIsReadFalse(Long profileId);

    // Cập nhật trạng thái isRead = true cho tất cả tin nhắn chưa đọc của profile
    @Transactional
    @Modifying
    @Query("UPDATE GuestbookMessage m SET m.isRead = true WHERE m.profile.id = :profileId AND m.isRead = false")
    void markAllAsReadByProfileId(@Param("profileId") Long profileId);
}