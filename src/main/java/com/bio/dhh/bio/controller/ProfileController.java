package com.bio.dhh.bio.controller;

import com.bio.dhh.bio.dto.AnalyticsDTO;
import com.bio.dhh.bio.dto.GuestbookMessageDTO;
import com.bio.dhh.bio.dto.ProfileUpdateRequestDTO;
import com.bio.dhh.bio.dto.SettingsUpdateDTO;
import com.bio.dhh.bio.model.ContentBlock;
import com.bio.dhh.bio.model.GuestbookMessage;
import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.repository.ClickCount;
import com.bio.dhh.bio.repository.ClickLogRepository;
import com.bio.dhh.bio.repository.GuestbookMessageRepository;
import com.bio.dhh.bio.repository.ProfileRepository;
import com.bio.dhh.bio.service.ProfileService;
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
    private final ProfileService profileService;
    private final GuestbookMessageRepository guestbookMessageRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Autowired
    public ProfileController(ProfileRepository profileRepository, ClickLogRepository clickLogRepository, ProfileService profileService, GuestbookMessageRepository guestbookMessageRepository) {
        this.profileRepository = profileRepository;
        this.clickLogRepository = clickLogRepository;
        this.profileService = profileService;
        this.guestbookMessageRepository = guestbookMessageRepository;
    }

    private String generateSlug(String input) {
        if (input == null || input.isEmpty()) { return "profile-" + System.currentTimeMillis(); }
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

            if (!profileToUpdate.getSlug().equals(profileData.getSlug()) && profileRepository.existsBySlugAndIdNot(profileData.getSlug(), profileToUpdate.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "URL tùy chỉnh này đã được sử dụng.");
            }

            profileToUpdate.setDisplayName(profileData.getDisplayName());
            profileToUpdate.setDescription(profileData.getDescription());
            profileToUpdate.setSlug(profileData.getSlug());
            profileToUpdate.setBackground(profileData.getBackground());
            profileToUpdate.setBackgroundImageOpacity(profileData.getBackgroundImageOpacity());
            profileToUpdate.setButtonStyle(profileData.getButtonStyle());
            profileToUpdate.setFont(profileData.getFont());
            profileToUpdate.setSeoTitle(profileData.getSeoTitle());
            profileToUpdate.setSeoDescription(profileData.getSeoDescription());
            profileToUpdate.setSocialImage(profileData.getSocialImage());
            profileToUpdate.setGoogleAnalyticsId(profileData.getGoogleAnalyticsId());
            profileToUpdate.setFacebookPixelId(profileData.getFacebookPixelId());

            return profileRepository.save(profileToUpdate);
        } else {
            Profile newProfile = new Profile();
            newProfile.setUserId(profileData.getUserId());
            newProfile.setDisplayName(profileData.getDisplayName());
            newProfile.setDescription(profileData.getDescription());
            newProfile.setBackground(profileData.getBackground());
            newProfile.setBackgroundImageOpacity(profileData.getBackgroundImageOpacity());
            newProfile.setButtonStyle(profileData.getButtonStyle());
            newProfile.setFont(profileData.getFont());
            newProfile.setSeoTitle(profileData.getSeoTitle());
            newProfile.setSeoDescription(profileData.getSeoDescription());
            newProfile.setSocialImage(profileData.getSocialImage());
            newProfile.setGoogleAnalyticsId(profileData.getGoogleAnalyticsId());
            newProfile.setFacebookPixelId(profileData.getFacebookPixelId());

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

    @PostMapping("/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody SettingsUpdateDTO settingsDTO) {
        Profile profile = profileRepository.findByUserId(settingsDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy profile"));

        if (settingsDTO.getShowStats() != null) {
            profile.setShowStats(settingsDTO.getShowStats());
        }
        if (settingsDTO.getEmailNotifications() != null) {
            profile.setEmailNotifications(settingsDTO.getEmailNotifications());
        }
        if (settingsDTO.getAnalyticsEnabled() != null) {
            profile.setAnalyticsEnabled(settingsDTO.getAnalyticsEnabled());
        }
        if (settingsDTO.getPublicProfile() != null) {
            profile.setPublicProfile(settingsDTO.getPublicProfile());
        }

        profileRepository.save(profile);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật cài đặt thành công"
        ));
    }

    @PostMapping("/{slug}/view")
    public ResponseEntity<Profile> recordProfileView(@PathVariable String slug) { // <-- Thay đổi kiểu trả về
        Profile updatedProfile = profileService.recordProfileView(slug); // <-- Nhận lại profile
        return ResponseEntity.ok(updatedProfile); // <-- Trả về cho Front-end
    }

    @GetMapping("/analytics/{userId}")
    public ResponseEntity<AnalyticsDTO> getAnalytics(@PathVariable String userId) {
        AnalyticsDTO analytics = profileService.getAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

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
        Map<Long, Long> stats = clickLogRepository.countByBlockIdIn(blockIds).stream()
                .collect(Collectors.toMap(ClickCount::getBlockId, ClickCount::getCount));
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{slug}/guestbook")
    public ResponseEntity<GuestbookMessage> addGuestbookMessage(@PathVariable String slug, @Valid @RequestBody GuestbookMessageDTO messageDTO) {
        Profile profile = profileRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile không tồn tại"));

        GuestbookMessage newMessage = new GuestbookMessage();
        newMessage.setProfile(profile);
        newMessage.setAuthorName(messageDTO.getAuthorName());
        newMessage.setMessageContent(messageDTO.getMessageContent());
        newMessage.setIsPublic(messageDTO.getIsPublic());

        guestbookMessageRepository.save(newMessage);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMessage);
    }

    @GetMapping("/{slug}/guestbook/public")
    public ResponseEntity<List<GuestbookMessage>> getPublicGuestbookMessages(@PathVariable String slug) {
        Profile profile = profileRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile không tồn tại"));
        List<GuestbookMessage> messages = guestbookMessageRepository.findByProfileIdAndIsPublicTrueOrderByCreatedAtDesc(profile.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/guestbook/mine/{userId}")
    public ResponseEntity<List<GuestbookMessage>> getMyGuestbookMessages(@PathVariable String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile không tồn tại"));
        List<GuestbookMessage> messages = guestbookMessageRepository.findByProfileIdOrderByCreatedAtDesc(profile.getId());
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/guestbook/{messageId}")
    public ResponseEntity<Void> deleteGuestbookMessage(@PathVariable Long messageId, @RequestParam String userId) {
        GuestbookMessage message = guestbookMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tin nhắn không tồn tại"));

        if (!message.getProfile().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xóa tin nhắn này.");
        }

        guestbookMessageRepository.delete(message);
        return ResponseEntity.noContent().build();
    }
    // Endpoint để lấy số tin nhắn chưa đọc
    @GetMapping("/guestbook/unread-count/{userId}")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(@PathVariable String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile không tồn tại"));

        long unreadCount = guestbookMessageRepository.countByProfileIdAndIsReadFalse(profile.getId());

        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    // Endpoint để đánh dấu tất cả là đã đọc
    @PostMapping("/guestbook/mark-as-read/{userId}")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile không tồn tại"));

        guestbookMessageRepository.markAllAsReadByProfileId(profile.getId());

        return ResponseEntity.ok().build();
    }@PostMapping("/guestbook/comment-as-author")
    public ResponseEntity<GuestbookMessage> addAuthorComment(@Valid @RequestBody GuestbookMessageDTO messageDTO, @RequestParam String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile không tồn tại"));

        // Cần có logic xác thực để đảm bảo người gửi request chính là chủ của userId này

        GuestbookMessage newMessage = new GuestbookMessage();
        newMessage.setProfile(profile);
        newMessage.setAuthorName(profile.getDisplayName()); // Tự động lấy tên tác giả
        newMessage.setMessageContent(messageDTO.getMessageContent());
        newMessage.setIsPublic(true); // Comment của tác giả luôn công khai
        newMessage.setIsRead(true);   // Tự động đánh dấu là đã đọc
        newMessage.setIsAuthor(true); // Đánh dấu đây là comment của tác giả

        guestbookMessageRepository.save(newMessage);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMessage);
    }

}