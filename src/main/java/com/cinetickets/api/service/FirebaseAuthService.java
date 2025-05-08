package com.cinetickets.api.service;

import com.cinetickets.api.dto.response.AuthResponse;
import com.cinetickets.api.dto.response.UserResponse;
import com.cinetickets.api.entity.User;
import com.cinetickets.api.repository.UserRepository;
import com.cinetickets.api.security.jwt.JwtTokenProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FirebaseAuth firebaseAuth;

    @Transactional
    public AuthResponse authenticateWithFirebase(String firebaseToken) {
        try {
            // Verificar el token de Firebase
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(firebaseToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            
            // Dividir el nombre completo en nombre y apellido si es posible
            String firstName = name;
            String lastName = "";
            if (name != null && name.contains(" ")) {
                String[] parts = name.split(" ", 2);
                firstName = parts[0];
                lastName = parts[1];
            }
            
            // Buscar si el usuario ya existe por email
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;
            
            if (existingUser.isPresent()) {
                // Actualizar datos del usuario existente
                user = existingUser.get();
                user.setAuthProvider("firebase");
                user.setAuthProviderId(uid);
                
                // Opcionalmente actualizar otros campos si es necesario
                
                user = userRepository.save(user);
            } else {
                // Crear un nuevo usuario
                user = User.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .authProvider("firebase")
                        .authProviderId(uid)
                        .role(User.UserRole.CUSTOMER) // Rol predeterminado
                        .status(User.UserStatus.ACTIVE)
                        .build();
                
                user = userRepository.save(user);
            }
            
            // Crear autenticación para generar token JWT
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
            
            // Generar token JWT para nuestro sistema
            String jwt = jwtTokenProvider.generateToken(authentication);
            
            // Crear respuesta con token y datos del usuario
            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole().name())
                    .build();
            
            return new AuthResponse(jwt, userResponse);
            
        } catch (FirebaseAuthException e) {
            log.error("Error validando token de Firebase", e);
            throw new RuntimeException("Error al autenticar con Firebase: " + e.getMessage());
        }
    }
}