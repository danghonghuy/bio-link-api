package com.bio.dhh.bio.controller;

import com.bio.dhh.bio.dto.ProfileUpdateRequestDTO;
import com.bio.dhh.bio.model.ContentBlock;
import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.repository.ClickCount;
import com.bio.dhh.bio.repository.ClickLogRepository;
import com.bio.dhh.bio.repository.ProfileRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileRepository profileRepository;
    private final ClickLogRepository clickLogRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Autowired
    public ProfileController(ProfileRepository profileRepository, ClickLogRepository clickLogRepository) {
        this.profileRepository = profileRepository;
        this.clickLogRepository = clickLogRepository;
    }

    private String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "profile-" + System.currentTimeMillis();
        }
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("đ", "d");
        return slug.isEmpty() ? "profile-" + System.currentTimeMillis() : slug;
    }

    @PostMapping
    public Profile createOrUpdateProfile(@Valid @RequestBody ProfileUpdateRequestDTO profileData) {
        Optional<Profile> existingProfileOpt = profileRepository.findByUserId(profileData.getUserId());

        if (existingProfileOpt.isPresent()) {
            Profile profileToUpdate = existingProfileOpt.get();

            if (!profileToUpdate.getSlug().equals(profileData.getSlug()) &&
                    profileRepository.existsBySlugAndIdNot(profileData.getSlug(), profileToUpdate.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "URL tùy chỉnh này đã được sử dụng.");
            }

            profileToUpdate.setDisplayName(profileData.getDisplayName());
            profileToUpdate.setDescription(profileData.getDescription());
            profileToUpdate.setSlug(profileData.getSlug());
            profileToUpdate.setBackground(profileData.getBackground()); // <-- THÊM DÒNG NÀY

            return profileRepository.save(profileToUpdate);
        } else {
            Profile newProfile = new Profile();
            newProfile.setUserId(profileData.getUserId());
            newProfile.setDisplayName(profileData.getDisplayName());
            newProfile.setDescription(profileData.getDescription());
            newProfile.setBackground(profileData.getBackground()); // <-- THÊM DÒNG NÀY

            String baseSlug = generateSlug(profileData.getDisplayName());
            String finalSlug = baseSlug;
            int counter = 1;
            while (profileRepository.findBySlug(finalSlug).isPresent()) {
                counter++;
                finalSlug = baseSlug + "-" + counter;
            }
            newProfile.setSlug(finalSlug);

            return profileRepository.save(newProfile);
        }
    }

    // Các hàm còn lại không đổi
    @PostMapping("/avatar")
    public Profile updateAvatar(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String avatarUrl = payload.get("avatarUrl");

        if (userId == null || avatarUrl == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId và avatarUrl là bắt buộc.");
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy profile"));

        profile.setAvatarUrl(avatarUrl);
        return profileRepository.save(profile);
    }

    @GetMapping("/mine/{userId}")
    public ResponseEntity<Profile> getMyProfile(@PathVariable String userId) {
        return profileRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Profile> getProfileBySlug(@PathVariable String slug) {
        return profileRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<Long, Long>> getStats(@PathVariable String userId) {
        Profile profile = profileRepository.findByUserId(userId).orElse(null);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        if (profile.getBlocks() == null || profile.getBlocks().isEmpty()) {
            return ResponseEntity.ok(Map.of());
        }

        List<Long> blockIds = profile.getBlocks().stream()
                .map(ContentBlock::getId)
                .collect(Collectors.toList());

        Map<Long, Long> stats = clickLogRepository.countByBlockIdIn(blockIds)
                .stream()
                .collect(Collectors.toMap(ClickCount::getBlockId, ClickCount::getCount));

        return ResponseEntity.ok(stats);
    }
}