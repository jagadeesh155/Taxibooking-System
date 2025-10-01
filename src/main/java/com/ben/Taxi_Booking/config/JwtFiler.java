package com.ben.Taxi_Booking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFiler extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFiler.class);
    private final SecretKey key;

    public JwtFiler(@Value("${jwt.secret-key}") String jwtSecretKey) {
        // Assuming you have a property 'jwt.secret-key' defined in application.properties/yml
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    // ⭐ NOTE: The shouldNotFilter method is intentionally omitted,
    // letting SecurityConfig manage path permissions.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Assuming JwtProviderUtil.JWT_HEADER is "Authorization"
        String jwt = request.getHeader("Authorization");

        if(jwt != null && jwt.startsWith("Bearer ")) {
            try {
                jwt = jwt.substring(7);

                Claims claims = Jwts.parser()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String username = String.valueOf(claims.get("email"));
                String authority = String.valueOf(claims.get("authority"));

                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);

                // Set authenticated token in the security context
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
            catch (Exception e) {
                logger.error("Token validation failed: {}", e.getMessage());
                // Throwing an exception here results in a 401/403 for protected paths
                throw new BadCredentialsException("Invalid token or signature: " + e.getMessage());
            }
        }

        // Pass the request down the chain. Spring Security will check the Authorization
        // context later. If the path is public, it will pass; otherwise, it requires a valid token.
        filterChain.doFilter(request, response);
    }
}
