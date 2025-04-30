package top.itaiit.embedding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.*;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.SpringAIModels;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
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
public class BgeEmbeddingModel extends AbstractEmbeddingModel {

    private static final Logger logger = LoggerFactory.getLogger(BgeEmbeddingModel.class);

    @Value("${spring.ai.openai.embedding.base-url}")
    private String baseUrl;
    @Value("${spring.ai.openai.embedding.embeddings-path}")
    private String apiUrl;

    private final RetryTemplate retryTemplate;
    private RestClient restClient;

    private final RestClient.Builder restClientBuilder = RestClient.builder();

    public BgeEmbeddingModel(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        restClient = restClientBuilder.baseUrl(baseUrl).build();
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
