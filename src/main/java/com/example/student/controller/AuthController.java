package com.example.student.controller;

import com.example.student.dto.ApiResponse;
import com.example.student.dto.AuthResponse;
import com.example.student.dto.LoginRequest;
import com.example.student.dto.RegisterRequest;
import com.example.student.dto.RefreshTokenRequest;
import com.example.student.dto.TokenRefreshResponse;
import com.example.student.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and refresh token rotation")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user profile (admin or standard user) and returns JWT credentials.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or duplicate email")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(
                ApiResponse.success("User registered successfully", response),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Log in as user", description = "Authenticates user credentials and returns active JWT token pair.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Authentication successful", response)
        );
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh expired access token", description = "Accepts a valid refresh token and yields new rotated access and refresh tokens.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(
                ApiResponse.success("Token refreshed successfully", response)
        );
    }
}
