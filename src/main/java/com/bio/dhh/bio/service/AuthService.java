package com.bio.dhh.bio.service;

import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.repository.ProfileRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final FirebaseAuth firebaseAuth;
    private final ProfileRepository profileRepository;

    @Autowired
    public AuthService(FirebaseAuth firebaseAuth, ProfileRepository profileRepository) {
        this.firebaseAuth = firebaseAuth;
        this.profileRepository = profileRepository;
    }

    public Profile verifyTokenAndCreateOrUpdateProfile(String firebaseIdToken) throws Exception {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(firebaseIdToken);
        String uid = decodedToken.getUid();

        Optional<Profile> existingProfileOpt = profileRepository.findByUserId(uid);

        if (existingProfileOpt.isPresent()) {
            return existingProfileOpt.get();
        } else {
            UserRecord userRecord = firebaseAuth.getUser(uid); // Lấy thêm thông tin từ Firebase
            Profile newProfile = new Profile();
            newProfile.setUserId(uid);
            newProfile.setDisplayName(userRecord.getDisplayName());
            newProfile.setAvatarUrl(userRecord.getPhotoUrl());

            String baseSlug = generateSlug(userRecord.getDisplayName());
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

    private String generateSlug(String input) {
        Pattern NONLATIN = Pattern.compile("[^\\w-]");
        Pattern WHITESPACE = Pattern.compile("[\\s]");
        if (input == null || input.isEmpty()) return "user-" + System.currentTimeMillis();
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("đ", "d");
        return slug.isEmpty() ? "user-" + System.currentTimeMillis() : slug;
    }
}