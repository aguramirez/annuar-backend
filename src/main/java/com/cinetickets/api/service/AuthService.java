package com.cinetickets.api.service;

import com.cinetickets.api.dto.request.RegisterRequest;
import com.cinetickets.api.entity.User;

public interface AuthService {
    
    User registerUser(RegisterRequest registerRequest);
    
    boolean isEmailRegistered(String email);
}