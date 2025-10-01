package com.ben.Taxi_Booking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtProviderUtil {

    private final SecretKey key;

    // ⭐ Defines the public static header constant
    public static final String JWT_HEADER = "Authorization";

    // Injects the key from application.properties
    public JwtProviderUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateJwtToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jwts = Jwts.builder()
                .setIssuer("The ben")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 60 * 60 * 1000))
                .claim("email", authentication.getName())
                .claim("authority", authorities)
                .signWith(key)
                .compact();

        return jwts;
    }

    public String getEmailFromToken(String token) {

        token = token.substring(7);

        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();

        String email = String.valueOf(claims.get("email"));

        return email;
    }
}