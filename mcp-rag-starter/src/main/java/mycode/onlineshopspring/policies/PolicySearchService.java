package mycode.onlineshopspring.policies;

import lombok.extern.slf4j.Slf4j;
import mycode.onlineshopspring.policies.dto.PolicyPassage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.rag.enabled", havingValue = "true")
public class PolicySearchService {

    private static final String POLICIES = "classpath:policies/*.md";
    private static final int TOP_K = 3;

    private final VectorStore vectorStore;

    public PolicySearchService(VectorStore vectorStore, ResourcePatternResolver resolver) throws IOException {
        this.vectorStore = vectorStore;
        List<Document> chunks = new ArrayList<>();
        for (Resource policy : resolver.getResources(POLICIES)) {
            String markdown = policy.getContentAsString(StandardCharsets.UTF_8);
            chunks.addAll(PolicyChunker.split(policy.getFilename(), markdown));
        }
        vectorStore.add(chunks);
        log.info("Indexed {} policy sections", chunks.size());
    }

    public List<PolicyPassage> search(String question) {
        SearchRequest request = SearchRequest.builder().query(question).topK(TOP_K).build();
        return vectorStore.similaritySearch(request).stream()
                .map(doc -> new PolicyPassage(
                        (String) doc.getMetadata().get(PolicyChunker.SOURCE),
                        (String) doc.getMetadata().get(PolicyChunker.SECTION),
                        Math.round(doc.getScore() * 1000) / 1000.0,
                        doc.getText()))
                .toList();
    }
}
