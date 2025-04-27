package com.cinetickets.api.security;

import com.cinetickets.api.entity.User;
import com.cinetickets.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return buildUserDetails(user);
    }

    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        try {
            User user = userRepository.findById(java.util.UUID.fromString(id))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

            return buildUserDetails(user);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid user id: " + id);
        }
    }

    private UserPrincipal buildUserDetails(User user) {
        // Verificar que el usuario esté activo
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("User account is not active: " + user.getEmail());
        }

        // Construir las autoridades (roles)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(Collections.singletonList(authority))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}