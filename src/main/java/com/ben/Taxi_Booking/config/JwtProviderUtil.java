package com.ben.Taxi_Booking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtProviderUtil {

    SecretKey key = Keys.hmacShaKeyFor(JwtSecurityContext.JWT_KEY.getBytes());

    public String generateJwtToken(Authentication authentication) {

        String jwts = Jwts.builder()
                .setIssuer("The ben")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 60 * 60 * 1000))
                .claim("email", authentication.getName())
                .signWith(key)
                .compact();

        return jwts;
    }

    public String getEmailFromToken(String token) {

        token = token.substring(7);

        Claims claims = Jwts.parser()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

        String email = String.valueOf(claims.get("email"));

        return token;
    }
}
