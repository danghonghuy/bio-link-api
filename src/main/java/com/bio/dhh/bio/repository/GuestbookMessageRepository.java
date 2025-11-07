// file: com/bio/dhh/bio/repository/GuestbookMessageRepository.java
package com.bio.dhh.bio.repository;

import com.bio.dhh.bio.model.GuestbookMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GuestbookMessageRepository extends JpaRepository<GuestbookMessage, Long> {
    List<GuestbookMessage> findByProfileIdAndIsPublicTrueOrderByCreatedAtDesc(Long profileId);
    List<GuestbookMessage> findByProfileIdOrderByCreatedAtDesc(Long profileId);
}