package mycode.onlineshopspring.orders.service;

import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.exceptions.OrderStatusNotAllowedException;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.orders.dto.OrdersResponse;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
import mycode.onlineshopspring.products.repository.ProductsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersCommandServiceImplTest {

    @Mock
    private OrdersRepository repository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductsRepository productsRepository;

    @Spy
    private OnlineShopMapper mapper;

    @InjectMocks
    private OrdersCommandServiceImpl service;

    @Test
    void cancelOrderCancelsPendingOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(repository.findById(orderId)).thenReturn(Optional.of(order(orderId, "PENDING")));
        when(repository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        OrdersResponse response = service.cancelOrder(orderId);

        // Assert
        assertEquals("CANCELLED", response.orderStatus());
    }

    @Test
    void cancelOrderRejectsDeliveredOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(repository.findById(orderId)).thenReturn(Optional.of(order(orderId, "DELIVERED")));

        // Act
        OrderStatusNotAllowedException ex = assertThrows(OrderStatusNotAllowedException.class,
                () -> service.cancelOrder(orderId));

        // Assert
        assertEquals("Only PENDING orders can be cancelled. Order " + orderId + " is DELIVERED.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    private Orders order(UUID id, String status) {
        Orders order = new Orders();
        order.setId(id);
        order.setOrderStatus(status);
        order.setOrder_date(LocalDate.now());
        return order;
    }
}
