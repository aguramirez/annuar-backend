package com.cinetickets.api.controller;

import com.cinetickets.api.dto.request.LoginRequest;
import com.cinetickets.api.dto.request.RegisterRequest;
import com.cinetickets.api.dto.response.ApiResponse;
import com.cinetickets.api.dto.response.AuthResponse;
import com.cinetickets.api.dto.response.UserResponse;
import com.cinetickets.api.entity.User;
import com.cinetickets.api.security.UserPrincipal;
import com.cinetickets.api.security.jwt.JwtTokenProvider;
import com.cinetickets.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        UserResponse userResponse = UserResponse.builder()
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .firstName(userPrincipal.getFirstName())
                .lastName(userPrincipal.getLastName())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority())
                .build();

        return ResponseEntity.ok(new AuthResponse(jwt, userResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authService.registerUser(registerRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{id}")
                .buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        // Esta funcionalidad requeriría implementar tokens de actualización
        // Por ahora, no lo implementamos en este ejemplo básico
        return ResponseEntity.badRequest().body(new AuthResponse(null, null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        // En implementaciones JWT estándares, no se mantiene estado en el servidor
        // El cliente simplemente descarta el token
        return ResponseEntity.ok(new ApiResponse(true, "Logout successful"));
    }
}