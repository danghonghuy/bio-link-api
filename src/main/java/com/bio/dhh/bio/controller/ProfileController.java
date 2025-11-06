package com.bio.dhh.bio.controller;

import com.bio.dhh.bio.model.ContentBlock;
import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.repository.ClickCount;
import com.bio.dhh.bio.repository.ClickLogRepository;
import com.bio.dhh.bio.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        if (input == null) return "";
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("đ", "d");
        return slug;
    }

    @PostMapping
    public Profile createOrUpdateProfile(@RequestBody Profile profileData) {
        Optional<Profile> existingProfile = profileRepository.findByUserId(profileData.getUserId());
        if (existingProfile.isPresent()) {
            Profile profileToUpdate = existingProfile.get();
            profileToUpdate.setDisplayName(profileData.getDisplayName());
            profileToUpdate.setDescription(profileData.getDescription());
            profileToUpdate.setAvatarUrl(profileData.getAvatarUrl());
            return profileRepository.save(profileToUpdate);
        } else {
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