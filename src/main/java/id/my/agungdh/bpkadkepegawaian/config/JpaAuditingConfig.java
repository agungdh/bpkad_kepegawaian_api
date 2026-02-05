package id.my.agungdh.bpkadkepegawaian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> {
            // Ambil user ID dari Security Context
            // Return system user (1) jika tidak ada authenticated user
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getName())) {
                try {
                    return Optional.of(Long.parseLong(authentication.getName()));
                } catch (NumberFormatException e) {
                    // Continue to default
                }
            }
            return Optional.of(1L); // Default system user
        };
    }
}
