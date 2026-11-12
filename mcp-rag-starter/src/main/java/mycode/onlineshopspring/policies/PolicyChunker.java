package mycode.onlineshopspring.policies;

import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PolicyChunker {

    public static final String SOURCE = "source";
    public static final String SECTION = "section";

    private PolicyChunker() {
    }

    public static List<Document> split(String fileName, String markdown) {
        String title = "";
        String section = null;
        StringBuilder body = new StringBuilder();
        List<Document> chunks = new ArrayList<>();

        for (String line : markdown.split("\\R")) {
            if (line.startsWith("## ")) {
                addChunk(chunks, fileName, title, section, body);
                section = line.substring(3).trim();
                body.setLength(0);
            } else if (line.startsWith("# ")) {
                title = line.substring(2).trim();
            } else if (section != null) {
                body.append(line).append('\n');
            }
        }
        addChunk(chunks, fileName, title, section, body);
        return chunks;
    }

    private static void addChunk(List<Document> chunks, String fileName, String title, String section, StringBuilder body) {
        if (section == null) return;
        String text = title + " - " + section + "\n" + body.toString().trim();
        chunks.add(new Document(text, Map.of(SOURCE, fileName, SECTION, section)));
    }
}
