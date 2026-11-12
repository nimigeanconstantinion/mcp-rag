package mycode.onlineshopspring.policies;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.rag.enabled", havingValue = "true")
public class RagConfig {

    @Bean
    public EmbeddingModel embeddingModel(@Value("${app.rag.model-uri}") String modelUri,
                                         @Value("${app.rag.tokenizer-uri}") String tokenizerUri,
                                         @Value("${app.rag.cache-dir}") String cacheDir) {
        TransformersEmbeddingModel model = new TransformersEmbeddingModel();
        model.setModelResource(modelUri);
        model.setTokenizerResource(tokenizerUri);
        model.setResourceCacheDirectory(cacheDir);
        return model;
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
