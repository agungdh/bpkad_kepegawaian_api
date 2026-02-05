package id.my.agungdh.bpkadkepegawaian.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BPKeD Kepegawaian API")
                        .version("1.0.0")
                        .description("API untuk Sistem Informasi Kepegawaian")
                        .contact(new Contact()
                                .name("BPKeD")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local"),
                        new Server().url("https://api.bpkad.go.id").description("Production")
                ));
    }
}
