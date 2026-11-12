package mycode.onlineshopspring.policies.dto;

public record PolicyPassage(
        String source,
        String section,
        double score,
        String text
) {
}
