package mycode.onlineshopspring.returns.service;

import mycode.onlineshopspring.exceptions.OrderDoesntExistException;
import mycode.onlineshopspring.exceptions.OrderStatusNotAllowedException;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
import mycode.onlineshopspring.returns.dto.ReturnRequestResponse;
import mycode.onlineshopspring.returns.models.ReturnRequest;
import mycode.onlineshopspring.returns.repository.ReturnRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnsCommandServiceImplTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ReturnRequestRepository returnRequestRepository;

    @InjectMocks
    private ReturnsCommandServiceImpl service;

    @Test
    void openReturnCreatesOpenRequestForDeliveredOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order(orderId, "DELIVERED")));
        when(returnRequestRepository.save(any(ReturnRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ReturnRequestResponse response = service.openReturn(orderId, "Wrong size");

        // Assert
        assertEquals(orderId, response.orderId());
        assertEquals("Wrong size", response.reason());
        assertEquals("OPEN", response.status());
    }

    @Test
    void openReturnRejectsShippedOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order(orderId, "SHIPPED")));

        // Act
        OrderStatusNotAllowedException ex = assertThrows(OrderStatusNotAllowedException.class,
                () -> service.openReturn(orderId, "Changed my mind"));

        // Assert
        assertEquals("Only DELIVERED orders can be returned. Order " + orderId + " is SHIPPED.", ex.getMessage());
        verify(returnRequestRepository, never()).save(any());
    }

    @Test
    void openReturnRejectsMissingOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(OrderDoesntExistException.class, () -> service.openReturn(orderId, "Broken"));
    }

    private Orders order(UUID id, String status) {
        Orders order = new Orders();
        order.setId(id);
        order.setOrderStatus(status);
        return order;
    }
}
