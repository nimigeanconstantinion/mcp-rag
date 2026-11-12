package mycode.onlineshopspring.mcp.dto;

import mycode.onlineshopspring.orders.dto.OrdersResponse;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record OrderView(
        UUID orderId,
        LocalDate orderDate,
        long daysSinceOrder,
        String status,
        int totalLei,
        List<OrderLineView> lines
) {
    public static OrderView from(OrdersResponse order) {
        List<OrderLineView> lines = order.orderDetailsSet().stream()
                .map(OrderLineView::from)
                .sorted(Comparator.comparing(OrderLineView::sku))
                .toList();
        long days = ChronoUnit.DAYS.between(order.order_date(), LocalDate.now());
        return new OrderView(order.id(), order.order_date(), days, order.orderStatus(), order.ammount(), lines);
    }
}
