package id.my.agungdh.bpkadkepegawaian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.rate-limit")
public class RateLimitProperties {

    private LoginLimit login = new LoginLimit();
    private GlobalLimit global = new GlobalLimit();
    private EndpointLimit endpoint = new EndpointLimit();

    @Data
    public static class LoginLimit {
        private int requests = 20;
        private int ttlSeconds = 60;
    }

    @Data
    public static class GlobalLimit {
        private int requests = 200;
        private int ttlSeconds = 60;
    }

    @Data
    public static class EndpointLimit {
        private int requests = 1000;
        private int ttlSeconds = 60;
    }
}
