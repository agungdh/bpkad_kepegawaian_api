package id.my.agungdh.bpkadkepegawaian.controller;

import id.my.agungdh.bpkadkepegawaian.dto.LoginRequest;
import id.my.agungdh.bpkadkepegawaian.dto.LoginResponse;
import id.my.agungdh.bpkadkepegawaian.dto.RefreshTokenRequest;
import id.my.agungdh.bpkadkepegawaian.dto.RegisterRequest;
import id.my.agungdh.bpkadkepegawaian.security.UserAuthentication;
import id.my.agungdh.bpkadkepegawaian.service.AuthService;
import id.my.agungdh.bpkadkepegawaian.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate token")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh authentication token")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info")
    public ResponseEntity<id.my.agungdh.bpkadkepegawaian.dto.UserData> me(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        id.my.agungdh.bpkadkepegawaian.dto.UserData userData = authService.getUserData(token);
        return ResponseEntity.ok(userData);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
