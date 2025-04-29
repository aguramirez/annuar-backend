package com.cinetickets.api.service.mock;

import com.cinetickets.api.entity.User;
import com.cinetickets.api.repository.UserRepository;
import com.cinetickets.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Implementación simulada de OAuth2UserService para desarrollo
 * Proporciona usuarios ficticios para pruebas de login social
 */
@Slf4j
@Service
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class MockOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Extraer qué proveedor está siendo usado (Google, Facebook, etc)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("MOCK OAUTH2: Processing login for provider: {}", registrationId);

        // Crear atributos simulados según el proveedor
        Map<String, Object> attributes = createMockAttributes(registrationId);
        
        // Buscar o crear usuario
        Optional<User> userOpt = userRepository.findByEmail((String) attributes.get("email"));
        User user;
        
        if (userOpt.isPresent()) {
            user = userOpt.get();
            log.info("MOCK OAUTH2: Found existing user: {}", user.getEmail());
        } else {
            // Crear nuevo usuario
            user = createMockUser(registrationId, attributes);
            log.info("MOCK OAUTH2: Created new user from OAuth2: {}", user.getEmail());
        }
        
        // Retornar un UserPrincipal simulado
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(null) // No se necesita para auth social
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())))
                .attributes(attributes)
                .build();
    }
    
    /**
     * Crea atributos simulados según el proveedor
     */
    private Map<String, Object> createMockAttributes(String provider) {
        Map<String, Object> attributes = new HashMap<>();
        
        // Definir datos simulados según el proveedor
        if ("google".equalsIgnoreCase(provider)) {
            attributes.put("sub", "g-" + UUID.randomUUID());
            attributes.put("name", "Juan Pérez");
            attributes.put("email", "juan.perez.google@example.com");
            attributes.put("picture", "https://via.placeholder.com/150");
        } else if ("facebook".equalsIgnoreCase(provider)) {
            attributes.put("id", "f-" + UUID.randomUUID());
            attributes.put("first_name", "María");
            attributes.put("last_name", "López");
            attributes.put("email", "maria.lopez.facebook@example.com");
            
            // Estructura simulada para imagen de perfil de Facebook
            Map<String, Object> picture = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            data.put("url", "https://via.placeholder.com/150");
            picture.put("data", data);
            attributes.put("picture", picture);
        } else {
            // Proveedor genérico
            attributes.put("id", "generic-" + UUID.randomUUID());
            attributes.put("name", "Usuario Prueba");
            attributes.put("email", "usuario.prueba@example.com");
        }
        
        return attributes;
    }
    
    /**
     * Crea un usuario simulado en la base de datos
     */
    private User createMockUser(String provider, Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String firstName, lastName;
        
        if ("google".equalsIgnoreCase(provider)) {
            String fullName = (String) attributes.get("name");
            String[] names = fullName.split(" ", 2);
            firstName = names[0];
            lastName = names.length > 1 ? names[1] : "";
        } else if ("facebook".equalsIgnoreCase(provider)) {
            firstName = (String) attributes.get("first_name");
            lastName = (String) attributes.get("last_name");
        } else {
            String fullName = (String) attributes.get("name");
            String[] names = fullName.split(" ", 2);
            firstName = names[0];
            lastName = names.length > 1 ? names[1] : "";
        }
        
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .passwordHash(null) // No se requiere para usuarios OAuth
                .role(User.UserRole.CUSTOMER)
                .status(User.UserStatus.ACTIVE)
                .authProvider(provider)
                .authProviderId((String) attributes.get("id") || attributes.get("sub"))
                .loyaltyPoints(0)
                .marketingConsent(false)
                .lastLogin(ZonedDateTime.now())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        return userRepository.save(user);
    }
}