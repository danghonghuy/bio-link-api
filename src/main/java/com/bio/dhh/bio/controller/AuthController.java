package com.bio.dhh.bio.controller;

import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/verify-token")
    public ResponseEntity<Profile> verifyToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("idToken");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Profile profile = authService.verifyTokenAndCreateOrUpdateProfile(token);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ", e);
        }
    }
}