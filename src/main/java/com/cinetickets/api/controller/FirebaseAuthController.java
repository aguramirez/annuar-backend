package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.FirebaseAuthRequest;
import com.cinetickets.api.dto.response.AuthResponse;
import com.cinetickets.api.service.FirebaseAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class FirebaseAuthController {

    private final FirebaseAuthService firebaseAuthService;

    @PostMapping("/firebase-auth")
    public ResponseEntity<AuthResponse> firebaseAuth(@Valid @RequestBody FirebaseAuthRequest authRequest) {
        AuthResponse response = firebaseAuthService.authenticateWithFirebase(authRequest.getFirebaseToken());
        return ResponseEntity.ok(response);
    }
}