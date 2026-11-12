package mycode.onlineshopspring.returns.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReturnRequestResponse(
        UUID id,
        UUID orderId,
        String reason,
        String status,
        LocalDateTime createdAt
) {
}
