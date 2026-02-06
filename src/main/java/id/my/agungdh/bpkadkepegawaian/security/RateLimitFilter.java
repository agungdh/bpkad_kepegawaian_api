package id.my.agungdh.bpkadkepegawaian.security;

import id.my.agungdh.bpkadkepegawaian.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(100)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    private static final String AUTH_PATH_PREFIX = "/api/auth/";
    private static final String[] EXCLUDED_PATHS = {
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = getClientIp(request);

        // Skip rate limiting for excluded paths
        if (isExcludedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check login rate limit for auth endpoints
        if (path.startsWith(AUTH_PATH_PREFIX)) {
            rateLimitService.checkLoginLimit(ip);
        }

        // For authenticated users, check global and endpoint limits
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication instanceof UserAuthentication userAuth) {

            Long userId = userAuth.getUserId();

            // Check global rate limit
            rateLimitService.checkGlobalLimit(userId);

            // Check endpoint-specific rate limit
            rateLimitService.checkEndpointLimit(userId, path);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract client IP address from request.
     * Handles X-Forwarded-For header for proxy scenarios.
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // If multiple IPs in X-Forwarded-For, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }

    /**
     * Check if path should be excluded from rate limiting.
     */
    private boolean isExcludedPath(String path) {
        for (String excluded : EXCLUDED_PATHS) {
            if (path.startsWith(excluded)) {
                return true;
            }
        }
        return false;
    }
}
