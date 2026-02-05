package id.my.agungdh.bpkadkepegawaian.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "id.my.agungdh.bpkadkepegawaian.repository")
public class JpaConfig {
    // Soft delete filter ditangkap di level repository (findByUuidAndDeletedAtIsNull)
}
