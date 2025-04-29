package top.itaiit.embedding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.*;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class BgeEmbeddingModel extends AbstractEmbeddingModel {

    private static final Logger logger = LoggerFactory.getLogger(BgeEmbeddingModel.class);

    @Autowired
    private RetryTemplate retryTemplate;

    private final RestClient.Builder restClientBuilder = RestClient.builder();
    private RestClient restClient;

    @Value("${spring.ai.openai.embedding.base-url}")
    private String baseUrl;
    @Value("${spring.ai.openai.embedding.embeddings-path}")
    private String apiUrl;

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        restClient = restClientBuilder.baseUrl(baseUrl).build();
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
