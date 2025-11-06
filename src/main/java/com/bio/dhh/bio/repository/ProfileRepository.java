package com.bio.dhh.bio.repository;

import com.bio.dhh.bio.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // Tìm kiếm profile dựa trên trường 'slug'
    Optional<Profile> findBySlug(String slug);

    // ▼▼▼ THÊM DÒNG NÀY VÀO ▼▼▼
    // Dạy cho Spring cách tìm profile dựa trên trường 'userId'
    Optional<Profile> findByUserId(String userId);

}