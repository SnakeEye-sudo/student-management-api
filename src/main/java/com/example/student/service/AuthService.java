package com.example.student.service;

import com.example.student.dto.AuthResponse;
import com.example.student.dto.LoginRequest;
import com.example.student.dto.RegisterRequest;
import com.example.student.dto.RefreshTokenRequest;
import com.example.student.dto.TokenRefreshResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    TokenRefreshResponse refreshToken(RefreshTokenRequest request);
}
