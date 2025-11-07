package com.bio.dhh.bio.repository;

import com.bio.dhh.bio.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {


    Optional<Profile> findBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
    Optional<Profile> findByUserId(String userId);
    @Modifying
    @Query("UPDATE Profile p SET p.views = p.views + 1 WHERE p.slug = :slug")
    void incrementViewsBySlug(@Param("slug") String slug);

}