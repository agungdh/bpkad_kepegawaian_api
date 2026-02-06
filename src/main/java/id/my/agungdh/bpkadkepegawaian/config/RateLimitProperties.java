package id.my.agungdh.bpkadkepegawaian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.rate-limit")
public class RateLimitProperties {

    private LoginLimit login = new LoginLimit(20, 60);
    private GlobalLimit global = new GlobalLimit(1000, 60);
    private EndpointLimit endpoint = new EndpointLimit(200, 60);

    @Data
    public static class LoginLimit {
        private int requests;
        private int ttlSeconds;

        public LoginLimit() {
        }

        public LoginLimit(int requests, int ttlSeconds) {
            this.requests = requests;
            this.ttlSeconds = ttlSeconds;
        }
    }

    @Data
    public static class GlobalLimit {
        private int requests;
        private int ttlSeconds;

        public GlobalLimit() {
        }

        public GlobalLimit(int requests, int ttlSeconds) {
            this.requests = requests;
            this.ttlSeconds = ttlSeconds;
        }
    }

    @Data
    public static class EndpointLimit {
        private int requests;
        private int ttlSeconds;

        public EndpointLimit() {
        }

        public EndpointLimit(int requests, int ttlSeconds) {
            this.requests = requests;
            this.ttlSeconds = ttlSeconds;
        }
    }
}
