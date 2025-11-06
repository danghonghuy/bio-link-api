package com.bio.dhh.bio.controller;

import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileRepository profileRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Autowired
    public ProfileController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // --- HÀM TẠO SLUG TÁCH RIÊNG ---
    private String generateSlug(String input) {
        if (input == null) return "";
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH)
                .replaceAll("đ", "d");
        return slug;
    }

    // === API TẠO MỚI HOẶC CẬP NHẬT PROFILE ===
    @PostMapping
    public Profile createOrUpdateProfile(@RequestBody Profile profileData) {

        Optional<Profile> existingProfile = profileRepository.findByUserId(profileData.getUserId());

        if (existingProfile.isPresent()) {
            // --- UPDATE (CẬP NHẬT) ---
            Profile profileToUpdate = existingProfile.get();
            profileToUpdate.setDisplayName(profileData.getDisplayName());
            profileToUpdate.setDescription(profileData.getDescription());
            profileToUpdate.setAvatarUrl(profileData.getAvatarUrl());
            profileToUpdate.setFacebookLink(profileData.getFacebookLink());
            profileToUpdate.setYoutubeLink(profileData.getYoutubeLink());
            profileToUpdate.setTiktokLink(profileData.getTiktokLink());
            profileToUpdate.setGithubLink(profileData.getGithubLink());
            profileToUpdate.setTheme(profileData.getTheme());
            // Không thay đổi slug khi update
            return profileRepository.save(profileToUpdate);

        } else {
            // --- CREATE (TẠO MỚI) ---
            String baseSlug = generateSlug(profileData.getDisplayName());
            String finalSlug = baseSlug;
            int counter = 1;

            while (profileRepository.findBySlug(finalSlug).isPresent()) {
                counter++;
                finalSlug = baseSlug + "-" + counter;
            }

            profileData.setSlug(finalSlug);
            return profileRepository.save(profileData);
        }
    }

    // === API MỚI: Lấy profile của người dùng đang đăng nhập ===
    @GetMapping("/mine/{userId}")
    public ResponseEntity<Profile> getMyProfile(@PathVariable String userId) {
        return profileRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // === API xem bio công khai (giữ nguyên) ===
    @GetMapping("/{slug}")
    public ResponseEntity<Profile> getProfileBySlug(@PathVariable String slug) {
        Optional<Profile> profileOptional = profileRepository.findBySlug(slug);

        return profileOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}