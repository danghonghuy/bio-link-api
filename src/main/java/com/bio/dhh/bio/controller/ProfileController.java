package com.bio.dhh.bio.controller;

import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profiles") // Đặt tiền tố chung ở đây
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @PostMapping // URL vẫn là /api/profiles
    public Profile createProfile(@RequestBody Profile profileData) {
        // --- LOGIC TẠO SLUG NÂNG CẤP ---
        String baseSlug = profileData.getDisplayName().toLowerCase()
                .replaceAll("đ", "d") // Chuyển 'đ' thành 'd'
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("\\s+", "-") // Thay dấu cách bằng gạch nối
                .replaceAll("[^a-z0-9-]", ""); // Bỏ ký tự lạ

        String finalSlug = baseSlug;
        int counter = 1;

        // Vòng lặp kiểm tra: nếu slug đã tồn tại thì thêm số vào đuôi
        while (profileRepository.findBySlug(finalSlug).isPresent()) {
            counter++;
            finalSlug = baseSlug + "-" + counter;
        }

        profileData.setSlug(finalSlug);
        // --- KẾT THÚC LOGIC NÂNG CẤP ---

        // Lưu profile vào database và trả về kết quả
        return profileRepository.save(profileData);
    }

    // API MỚI CỦA BẠN ĐÂY
    @GetMapping("/{slug}")
    public ResponseEntity<Profile> getProfileBySlug(@PathVariable String slug) {
        Optional<Profile> profileOptional = profileRepository.findBySlug(slug);

        return profileOptional
                .map(ResponseEntity::ok) // Nếu có profile, trả về OK(profile)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Nếu không, trả về Not Found
    }
}