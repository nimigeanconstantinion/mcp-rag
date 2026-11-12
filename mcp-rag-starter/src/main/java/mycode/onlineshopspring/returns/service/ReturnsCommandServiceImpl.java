package mycode.onlineshopspring.returns.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.exceptions.OrderDoesntExistException;
import mycode.onlineshopspring.exceptions.OrderStatusNotAllowedException;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
import mycode.onlineshopspring.returns.dto.ReturnRequestResponse;
import mycode.onlineshopspring.returns.models.ReturnRequest;
import mycode.onlineshopspring.returns.repository.ReturnRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnsCommandServiceImpl implements ReturnsCommandService {

    private static final String DELIVERED = "DELIVERED";
    private static final String OPEN = "OPEN";

    private final OrdersRepository ordersRepository;
    private final ReturnRequestRepository returnRequestRepository;

    @Transactional
    @Override
    public ReturnRequestResponse openReturn(UUID orderId, String reason) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(OrderDoesntExistException::new);
        if (!DELIVERED.equals(order.getOrderStatus())) {
            throw new OrderStatusNotAllowedException(
                    "Only DELIVERED orders can be returned. Order " + orderId + " is " + order.getOrderStatus() + ".");
        }

        ReturnRequest request = new ReturnRequest();
        request.setOrder(order);
        request.setReason(reason);
        request.setStatus(OPEN);
        ReturnRequest saved = returnRequestRepository.save(request);

        return new ReturnRequestResponse(saved.getId(), orderId, saved.getReason(), saved.getStatus(), saved.getCreatedAt());
    }
}
