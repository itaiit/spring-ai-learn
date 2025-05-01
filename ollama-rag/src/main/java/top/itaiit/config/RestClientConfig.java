package top.itaiit.config;

import org.springframework.ai.model.ollama.autoconfigure.OllamaConnectionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@Configuration
@EnableConfigurationProperties(OllamaConnectionProperties.class)
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder, OllamaConnectionProperties ollamaConnectionProperties) {
        Consumer<HttpHeaders> finalHeaders = h -> {
            h.setContentType(MediaType.APPLICATION_JSON);
        };
        return builder
                .baseUrl(ollamaConnectionProperties.getBaseUrl())
                .defaultHeaders(finalHeaders)
                .build();
    }

}
