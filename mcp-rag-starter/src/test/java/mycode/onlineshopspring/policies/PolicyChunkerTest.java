package mycode.onlineshopspring.policies;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolicyChunkerTest {

    private static final String MARKDOWN = """
            # Livrarea

            Intro text that belongs to no section.

            ## Unde livrăm

            Livrăm doar în România.

            ## Cât costă

            Sub 300 de lei: 19 lei.
            """;

    @Test
    void splitCreatesOneChunkPerSection() {
        // Arrange
        // Act
        List<Document> chunks = PolicyChunker.split("livrare.md", MARKDOWN);

        // Assert
        assertEquals(2, chunks.size());
        assertEquals("Livrarea - Unde livrăm\nLivrăm doar în România.", chunks.get(0).getText());
        assertEquals("Livrarea - Cât costă\nSub 300 de lei: 19 lei.", chunks.get(1).getText());
    }

    @Test
    void splitKeepsSourceAndSectionInMetadata() {
        // Arrange
        // Act
        List<Document> chunks = PolicyChunker.split("livrare.md", MARKDOWN);

        // Assert
        assertEquals("livrare.md", chunks.get(1).getMetadata().get(PolicyChunker.SOURCE));
        assertEquals("Cât costă", chunks.get(1).getMetadata().get(PolicyChunker.SECTION));
    }

    @Test
    void splitReturnsNothingWithoutSections() {
        // Arrange
        // Act
        List<Document> chunks = PolicyChunker.split("empty.md", "# Title\n\nOnly an intro.");

        // Assert
        assertEquals(0, chunks.size());
    }
}
