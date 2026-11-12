package mycode.onlineshopspring.mcp.dto;

import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;

public record OrderLineView(
        String sku,
        String productName,
        String category,
        int quantity,
        int unitPrice
) {
    public static OrderLineView from(OrderDetailsResponse line) {
        return new OrderLineView(line.sku(), line.product().name(), line.product().category(),
                line.quantity(), line.price());
    }
}
