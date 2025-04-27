package com.cinetickets.api.security.oauth2;

import com.cinetickets.api.entity.User;
import com.cinetickets.api.exception.OAuth2AuthenticationProcessingException;
import com.cinetickets.api.repository.UserRepository;
import com.cinetickets.api.security.UserPrincipal;
import com.cinetickets.api.security.oauth2.user.OAuth2UserInfo;
import com.cinetickets.api.security.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Lanzar una excepción de autenticación con un mensaje genérico
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            
            // Si el usuario existe pero se registró usando un método diferente
            if (!user.getAuthProvider().equals(registrationId)) {
                throw new OAuth2AuthenticationProcessingException(
                    "You're signed up with " + user.getAuthProvider() + 
                    " account. Please use your " + user.getAuthProvider() + " account to login."
                );
            }
            
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(null)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())))
                .attributes(oAuth2User.getAttributes())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(oAuth2UserInfo.getEmail())
                .firstName(oAuth2UserInfo.getFirstName())
                .lastName(oAuth2UserInfo.getLastName())
                .profileImage(oAuth2UserInfo.getImageUrl())
                .authProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId())
                .authProviderId(oAuth2UserInfo.getId())
                .role(User.UserRole.CUSTOMER)
                .status(User.UserStatus.ACTIVE)
                .loyaltyPoints(0)
                .marketingConsent(false)
                .lastLogin(ZonedDateTime.now())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setProfileImage(oAuth2UserInfo.getImageUrl());
        existingUser.setLastLogin(ZonedDateTime.now());
        existingUser.setUpdatedAt(ZonedDateTime.now());
        
        return userRepository.save(existingUser);
    }
}