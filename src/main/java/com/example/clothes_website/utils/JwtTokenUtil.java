package com.example.clothes_website.utils;

import com.example.clothes_website.security.TokenPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    public static final String ACCESS_TOKEN = "ACCESS";
    public static final String REFRESH_TOKEN = "REFRESH";

    @Value("${secret_jwt}")
    private String secret;

    public String generateToken(TokenPayload tokenPayload, long expirationSeconds) {
        return generateToken(tokenPayload, expirationSeconds, ACCESS_TOKEN);
    }

    public String generateAccessToken(TokenPayload tokenPayload, long expirationSeconds) {
        return generateToken(tokenPayload, expirationSeconds, ACCESS_TOKEN);
    }

    public String generateRefreshToken(TokenPayload tokenPayload, long expirationSeconds) {
        return generateToken(tokenPayload, expirationSeconds, REFRESH_TOKEN);
    }

    public String generateToken(TokenPayload tokenPayload, long expirationSeconds, String tokenType) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(tokenPayload.getUserId()))
                .claim("userId", tokenPayload.getUserId())
                .claim("fullName", tokenPayload.getFullName())
                .claim("role", tokenPayload.getRole())
                .claim("tokenType", tokenType)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationSeconds * 1000L))
                .signWith(getSigningKey())
                .compact();
    }

    public TokenPayload getTokenPayload(String token) {
        Claims claims = getClaimsFromToken(token, Function.identity());
        Number userId = claims.get("userId", Number.class);
        String fullName = claims.get("fullName", String.class);
        String role = claims.get("role", String.class);

        if (userId == null || fullName == null || role == null) {
            throw new JwtException("JWT payload is incomplete");
        }

        return TokenPayload.builder()
                .userId(userId.intValue())
                .fullName(fullName)
                .role(role)
                .build();
    }

    public String getTokenType(String token) {
        return getClaimsFromToken(token, claims -> claims.get("tokenType", String.class));
    }

    public <T> T getClaimsFromToken(String token, Function<Claims, T> claimResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimResolver.apply(claims);
    }

    public boolean isValidToken(String token, TokenPayload expectedPayload) {
        try {
            TokenPayload actualPayload = getTokenPayload(token);
            return ACCESS_TOKEN.equals(getTokenType(token))
                    && actualPayload.getUserId() == expectedPayload.getUserId()
                    && expectedPayload.getFullName().equals(actualPayload.getFullName());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimsFromToken(token, Claims::getExpiration);
            return expiration == null || expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
