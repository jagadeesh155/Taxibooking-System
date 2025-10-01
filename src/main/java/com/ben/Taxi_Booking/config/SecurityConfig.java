package com.ben.Taxi_Booking.config;

import com.ben.Taxi_Booking.config.JwtFiler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// Use @EnableWebSecurity if needed, but often not required with Boot 3.4
public class SecurityConfig {

    private final JwtFiler jwtFiler;

    // Inject JwtFiler via constructor
    public SecurityConfig(JwtFiler jwtFiler) {
        this.jwtFiler = jwtFiler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Configure Authorization Rules FIRST
        http.authorizeHttpRequests(authorize -> authorize
                // ⭐ FINAL DEFINITIVE FIX: Explicitly list the sign-up path using a strict matcher
                // This removes all ambiguity and is the most reliable way to permit a specific public endpoint.
                .requestMatchers("/api/auth/driver/signup").permitAll()
                .requestMatchers("/api/auth/login").permitAll()

                // Keep the general rule for safety and future endpoints
                .requestMatchers("/api/auth/**").permitAll()

                // PROTECTED ACCESS: All other requests must be authenticated
                .anyRequest().authenticated()
        );

        // 2. Configure Session Management (Stateless for JWT)
        http.sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 3. Add JWT Filter
        // Add the custom JWT filter BEFORE Spring Security's standard filter
        http.addFilterBefore(jwtFiler, UsernamePasswordAuthenticationFilter.class);

        // 4. Disable CSRF (Stateless APIs do not need it)
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

