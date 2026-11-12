package mycode.onlineshopspring.mcp.dto;

import mycode.onlineshopspring.customer.dto.CustomerResponse;

public record CustomerView(
        String fullName,
        String email,
        String phone,
        String country,
        String shippingAddress,
        int orderCount
) {
    public static CustomerView from(CustomerResponse customer) {
        return new CustomerView(customer.fullName(), customer.email(), customer.phone(), customer.country(),
                customer.defaultShippingAddress(), customer.orderSet().size());
    }
}
