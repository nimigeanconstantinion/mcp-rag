package mycode.onlineshopspring.mcp;

import mycode.onlineshopspring.customer.service.UserQuerryService;
import mycode.onlineshopspring.mcp.dtos.OrdersView;
import mycode.onlineshopspring.mcp.dtos.ProductView;
import mycode.onlineshopspring.orders.dto.OrdersResponse;
import mycode.onlineshopspring.orders.service.OrdersQuerryService;
import mycode.onlineshopspring.products.service.ProductsQuerryService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ShopTools {
    ProductsQuerryService productsQuerryService;
    OrdersQuerryService ordersQuerryService;
    UserQuerryService userQuerryService;

    public ShopTools(ProductsQuerryService productsQuerryService, UserQuerryService userQuerryService,OrdersQuerryService ordersQuerryService ) {
        this.productsQuerryService = productsQuerryService;
        this.userQuerryService = userQuerryService;
        this.ordersQuerryService=ordersQuerryService;
    }

    @McpTool(name = "search_products",
            description = "Searches products by text in the name, sku or category (product names are in Romanian). An empty query returns all products. Returns sku, name, category, price in lei, stock and description.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public List<ProductView> searchProducts(
            @McpToolParam(description = "Text to search for, e.g. a sku like LAP-002, a category like Audio, or part of a name", required = false) String query) {
        return productsQuerryService.searchProducts(query).stream().map(ProductView::from).toList();
    }

    @McpTool(name = "customer_orders",
            description = "Returns the orders of the customer with the given email address, newest first. Each order has its id, date, status (PENDING, SHIPPED, DELIVERED, CANCELLED), amount in lei, shipping address and the ordered products (sku, name, quantity, unit price in lei). Fails if no customer has that email.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public OrdersView customerOrders(
            @McpToolParam(description = "Email address of the customer, e.g. ana.pop@example.com", required = true) String email) {
        return OrdersView.from(userQuerryService.findCustomerByEmail(email.trim()));
    }

    @McpTool(name = "order_by_status",
            description = "Returns the orders by status  (PENDING, SHIPPED, DELIVERED, CANCELLED).",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public List<OrdersResponse> byStatus(
            @McpToolParam(description = "PENDING, SHIPPED, DELIVERED, CANCELLED",
            required = true) String status,
            @McpToolParam(description = "The shipment id",
                                               required = false) UUID shipmentId) {
        return ordersQuerryService.findOrdersByStatus(status);

    }

    @McpTool(name = "find_customer",
            description = "Returns Customers by Name nu si comenzile. Daca numele este gol , intoarce raspunsul nu ai introdus numele clientului. Nu tii cont de 
            majuscule minuscule. numele poate fi partial si returnezi clientii in ordine descrescatoare a potrivirii",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public List<CustomerResponse> fimdCustomerByName(
            @McpToolParam(description = "Name of Customer. Ex. Daniel",
            required = true) String name) {
                
             List<CustomerResponse> lista=UserQuerryService.

    }

}
