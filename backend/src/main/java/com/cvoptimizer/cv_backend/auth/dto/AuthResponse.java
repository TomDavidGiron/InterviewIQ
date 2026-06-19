package com.cvoptimizer.cv_backend.auth.dto;

public class AuthResponse {
    private String token;
    private String userId;
    private String username;

    public AuthResponse(String token, String userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
}
