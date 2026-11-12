package mycode.onlineshopspring.returns.service;

import mycode.onlineshopspring.returns.dto.ReturnRequestResponse;

import java.util.UUID;

public interface ReturnsCommandService {
    ReturnRequestResponse openReturn(UUID orderId, String reason);
}
