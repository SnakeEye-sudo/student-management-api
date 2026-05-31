package com.example.student.service.impl;

import com.example.student.dto.AuthResponse;
import com.example.student.dto.LoginRequest;
import com.example.student.dto.RegisterRequest;
import com.example.student.dto.RefreshTokenRequest;
import com.example.student.dto.TokenRefreshResponse;
import com.example.student.model.Role;
import com.example.student.model.User;
import com.example.student.model.RefreshToken;
import com.example.student.repository.UserRepository;
import com.example.student.repository.RefreshTokenRepository;
import com.example.student.security.JwtService;
import com.example.student.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email address is already in use. Please use a unique email.");
        }

        Role role = Role.ROLE_USER;
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                String roleStr = request.getRole().trim().toUpperCase();
                if (!roleStr.startsWith("ROLE_")) {
                    roleStr = "ROLE_" + roleStr;
                }
                role = Role.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role provided. Allowed roles: USER, ADMIN (or ROLE_USER, ROLE_ADMIN)");
            }
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        user = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        String tokenStr = request.getRefreshToken();
        RefreshToken token = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token is not valid. Please sign in again."));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new IllegalArgumentException("Refresh token was expired. Please make a new sign in request.");
        }

        User user = token.getUser();
        String accessToken = jwtService.generateToken(user);
        
        // Optional/Recommended: Rotate refresh token on usage
        RefreshToken rotatedRefreshToken = createRefreshToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rotatedRefreshToken.getToken())
                .build();
    }

    private RefreshToken createRefreshToken(User user) {
        // Find existing token for this user to update it in-place, or create a new one
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));

        return refreshTokenRepository.save(refreshToken);
    }
}
