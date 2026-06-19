package com.cvoptimizer.cv_backend.auth.config;

import com.cvoptimizer.cv_backend.auth.jwt.JwtAuthenticationFilter;
import com.cvoptimizer.cv_backend.auth.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserService userService;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, UserService userService) {
        this.jwtFilter = jwtFilter;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/interview/topics").permitAll()
                        .requestMatchers("/api/interview/start").permitAll()
                        .requestMatchers("/api/interview/job-specific").permitAll()
                        .requestMatchers("/api/interview/*/answer").permitAll()
                        .requestMatchers("/api/interview/*/summary").permitAll()
                        .requestMatchers("/api/interview/*/skill-graph").permitAll()
                        .requestMatchers("/api/scrape/**").permitAll()
                        .requestMatchers("/api/image-upload").permitAll()
                        .anyRequest().authenticated()
                )
                .userDetailsService(userService)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
