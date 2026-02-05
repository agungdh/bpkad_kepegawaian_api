package id.my.agungdh.bpkadkepegawaian.service;

import id.my.agungdh.bpkadkepegawaian.dto.LoginRequest;
import id.my.agungdh.bpkadkepegawaian.dto.LoginResponse;
import id.my.agungdh.bpkadkepegawaian.dto.RefreshTokenRequest;
import id.my.agungdh.bpkadkepegawaian.dto.UserData;
import id.my.agungdh.bpkadkepegawaian.entity.User;
import id.my.agungdh.bpkadkepegawaian.entity.UserRole;
import id.my.agungdh.bpkadkepegawaian.repository.UserRepository;
import id.my.agungdh.bpkadkepegawaian.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.security.token.ttl:86400}")
    private long tokenTtl;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        if (!user.getEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        // Get user roles
        Set<String> roles = userRoleRepository.findByUser_Id(user.getId()).stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());

        // Create user data for token
        UserData userData = UserData.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .pegawaiId(user.getPegawai() != null ? user.getPegawai().getId() : null)
                .build();

        // Generate and store token
        String token = tokenService.generateToken();
        tokenService.storeToken(token, userData);

        log.info("User logged in: {}", request.getUsername());

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .roles(roles)
                .expiresIn(tokenTtl)
                .build();
    }

    public void logout(String token) {
        tokenService.revokeToken(token);
        log.info("User logged out");
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        String newToken = tokenService.refreshToken(request.getToken());
        if (newToken == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        UserData userData = tokenService.getUserData(newToken);

        log.info("Token refreshed for user: {}", userData.getUsername());

        return LoginResponse.builder()
                .token(newToken)
                .username(userData.getUsername())
                .roles(userData.getRoles())
                .expiresIn(tokenTtl)
                .build();
    }

    public UserData getUserData(String token) {
        return tokenService.getUserData(token);
    }
}
