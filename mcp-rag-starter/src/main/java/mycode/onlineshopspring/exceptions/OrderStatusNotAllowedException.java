package mycode.onlineshopspring.exceptions;

public class OrderStatusNotAllowedException extends RuntimeException {
    public OrderStatusNotAllowedException(String message) {
        super(message);
    }
}
