package com.bio.dhh.bio.repository;

import com.bio.dhh.bio.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <<<< THÊM DÒNG NÀY

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // DẠY CHO NÓ PHƯƠNG THỨC MỚI BẰNG CÁCH KHAI BÁO Ở ĐÂY
    Optional<Profile> findBySlug(String slug);

}