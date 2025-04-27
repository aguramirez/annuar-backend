package com.cinetickets.api.service.impl;

import com.cinetickets.api.dto.request.RegisterRequest;
import com.cinetickets.api.entity.User;
import com.cinetickets.api.exception.ResourceAlreadyExistsException;
import com.cinetickets.api.repository.UserRepository;
import com.cinetickets.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        if (isEmailRegistered(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already registered");
        }
        
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
                .role(User.UserRole.CUSTOMER)
                .status(User.UserStatus.ACTIVE)
                .authProvider("local") // Proveedor local (no OAuth)
                .loyaltyPoints(0)
                .marketingConsent(registerRequest.getMarketingConsent() != null ? registerRequest.getMarketingConsent() : false)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }
}