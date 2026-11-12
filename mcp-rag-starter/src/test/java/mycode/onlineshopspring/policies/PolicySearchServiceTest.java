package mycode.onlineshopspring.policies;

import mycode.onlineshopspring.policies.dto.PolicyPassage;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PolicySearchServiceTest {

    private static final int DIMENSIONS = 512;

    @Test
    void searchReturnsThreePassagesWithSourceSectionAndScore() throws Exception {
        // Arrange
        PolicySearchService service = new PolicySearchService(
                SimpleVectorStore.builder(new WordCountEmbeddingModel()).build(),
                new PathMatchingResourcePatternResolver());

        // Act
        List<PolicyPassage> passages = service.search("Comenzi de valoare mare supervizor");

        // Assert
        assertEquals(3, passages.size());
        assertEquals("retur.md", passages.get(0).source());
        assertEquals("Comenzi de valoare mare", passages.get(0).section());
        assertTrue(passages.get(0).score() > passages.get(2).score());
    }

    private static final class WordCountEmbeddingModel implements EmbeddingModel {

        @Override
        public EmbeddingResponse call(EmbeddingRequest request) {
            List<String> texts = request.getInstructions();
            return new EmbeddingResponse(IntStream.range(0, texts.size())
                    .mapToObj(i -> new Embedding(vector(texts.get(i)), i))
                    .toList());
        }

        @Override
        public float[] embed(Document document) {
            return vector(document.getText());
        }

        private float[] vector(String text) {
            float[] vector = new float[DIMENSIONS];
            Arrays.stream(text.toLowerCase(Locale.ROOT).split("\\W+"))
                    .filter(word -> !word.isBlank())
                    .forEach(word -> vector[Math.floorMod(word.hashCode(), DIMENSIONS)] += 1);
            return vector;
        }
    }
}
