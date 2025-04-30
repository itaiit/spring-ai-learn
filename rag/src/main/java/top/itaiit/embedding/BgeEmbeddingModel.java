package top.itaiit.embedding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.*;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingProperties;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@ImportAutoConfiguration(classes = { SpringAiRetryAutoConfiguration.class, RestClientAutoConfiguration.class,
        WebClientAutoConfiguration.class })
@ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = "bge",
        matchIfMissing = false)
@EnableConfigurationProperties(OpenAiEmbeddingProperties.class)
public class BgeEmbeddingModel extends AbstractEmbeddingModel {

    private static final Logger logger = LoggerFactory.getLogger(BgeEmbeddingModel.class);

    private final String apiUrl;
    private final RetryTemplate retryTemplate;
    private final RestClient restClient;

    public BgeEmbeddingModel(OpenAiEmbeddingProperties embeddingProperties, RetryTemplate retryTemplate, RestClient restClient) {
        this.retryTemplate = retryTemplate;
        this.restClient = restClient;
        this.apiUrl = embeddingProperties.getEmbeddingsPath();
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        // 构造请求参数
        OpenAiApi.EmbeddingRequest<List<String>> embeddingRequest =
                new OpenAiApi.EmbeddingRequest<>(request.getInstructions(), "bge-small-zh-v1.5");
        OpenAiApi.EmbeddingList<OpenAiApi.Embedding> apiEmbeddingResponse = retryTemplate.execute(cx -> restClient.post()
                .uri(apiUrl)
                .body(embeddingRequest)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<OpenAiApi.EmbeddingList<OpenAiApi.Embedding>>() {
                })
                .getBody());
        Usage embeddingResponseUsage = new EmptyUsage();
        var metadata = new EmbeddingResponseMetadata(apiEmbeddingResponse.model(), embeddingResponseUsage);

        List<Embedding> embeddings = apiEmbeddingResponse.data()
                .stream()
                .map(e -> new Embedding(e.embedding(), e.index()))
                .toList();

        EmbeddingResponse embeddingResponse = new EmbeddingResponse(embeddings, metadata);
        return embeddingResponse;
    }

    @Override
    public float[] embed(Document document) {
        Assert.notNull(document, "Document must not be null");
        return this.embed(document.getFormattedContent(MetadataMode.EMBED));
    }
}
