package top.itaiit.config;

import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@Configuration
@EnableConfigurationProperties(OpenAiEmbeddingProperties.class)
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder, OpenAiEmbeddingProperties embeddingProperties) {
        Consumer<HttpHeaders> finalHeaders = h -> {
            h.setContentType(MediaType.APPLICATION_JSON);
        };
        return builder
                .baseUrl(embeddingProperties.getBaseUrl())
                .defaultHeaders(finalHeaders)
                .build();
    }

}
