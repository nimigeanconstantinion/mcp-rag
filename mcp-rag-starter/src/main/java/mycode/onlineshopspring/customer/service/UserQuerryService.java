package mycode.onlineshopspring.customer.service;

import mycode.onlineshopspring.customer.dto.CustomerListResponse;
import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.mcp.dto.CustomerView;

import java.util.List;
import java.util.UUID;

public interface UserQuerryService {
    CustomerListResponse findAllCustomers(int page, int size);
    CustomerResponse findCustomerById(UUID id);
    CustomerResponse findCustomerByEmail(String email);
    List<CustomerView> searchCustomers(String text);
    
}
