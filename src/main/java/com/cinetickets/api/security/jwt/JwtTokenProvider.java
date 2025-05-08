package com.cinetickets.api.security.jwt;

import com.cinetickets.api.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey jwtSecret;
    private final long jwtExpirationInMs;
    private final String jwtIssuer;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration}") long jwtExpirationInMs,
            @Value("${app.jwt.issuer}") String jwtIssuer) {

        // Generar una clave lo suficientemente segura para HS512
        // Usamos Keys.secretKeyFor para garantizar una clave segura
        this.jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.jwtExpirationInMs = jwtExpirationInMs;
        this.jwtIssuer = jwtIssuer;
    }

    public String generateToken(Authentication authentication) {
        String subject;
        Map<String, Object> claims = new HashMap<>();

        if (authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            subject = userPrincipal.getUsername();

            // Agregar claims específicos de UserPrincipal
            claims.put("id", userPrincipal.getId());
            claims.put("email", userPrincipal.getEmail());
            claims.put("firstName", userPrincipal.getFirstName());
            claims.put("lastName", userPrincipal.getLastName());
        } else {
            // Si es un String (email) u otro tipo
            subject = authentication.getName();
        }

        // Agregar roles a los claims
        claims.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecret)  // No es necesario especificar el algoritmo, ya que Keys.secretKeyFor define HS512
                .compact();
    }

    public String generateTokenForUser(UUID userId, String email, String role, String fullName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(userId.toString())
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("email", email)
                .claim("roles", role)
                .claim("name", fullName)
                .signWith(jwtSecret)
                .compact();
    }

    public String getUserIdFromJWT(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error parsing JWT: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Error validating JWT token: {}", ex.getMessage());
        }
        return false;
    }
}