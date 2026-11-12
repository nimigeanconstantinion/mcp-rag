package mycode.onlineshopspring.mcp.dtos;

import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;
import mycode.onlineshopspring.orders.dto.OrdersResponse;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record OrdersView(
        String customerEmail,
        String customerName,
        List<OrderView> orders
) {
    public record OrderView(
            UUID id,
            LocalDate orderDate,
            String status,
            int amount,
            String shippingAddress,
            List<OrderItemView> items
    ) {
        public static OrderView from(OrdersResponse order) {
            return new OrderView(order.id(), order.order_date(), order.orderStatus(), order.ammount(),
                    order.shippingAdress(), order.orderDetailsSet().stream()
                    .map(OrderItemView::from)
                    .sorted(Comparator.comparing(OrderItemView::sku))
                    .toList());
        }
    }

    public record OrderItemView(
            String sku,
            String name,
            int quantity,
            int price
    ) {
        public static OrderItemView from(OrderDetailsResponse detail) {
            String name = detail.product() != null ? detail.product().name() : null;
            return new OrderItemView(detail.sku(), name, detail.quantity(), detail.price());
        }
    }

    public static OrdersView from(CustomerResponse customer) {
        return new OrdersView(customer.email(), customer.fullName(), customer.orderSet().stream()
                .map(OrderView::from)
                .sorted(Comparator.comparing(OrderView::orderDate).reversed())
                .toList());
    }
}
