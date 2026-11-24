package mycode.onlineshopspring.mcp;

import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.customer.service.UserQuerryService;
import mycode.onlineshopspring.mcp.dto.CustomerView;
import mycode.onlineshopspring.mcp.dto.OrderView;
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
            description = "comenzile clientului, cele mai noi\n" +
                    "primele: orderId, orderDate,\n" +
                    "daysSinceOrder, status, totalLei, lines",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public OrdersView customerOrders(
            @McpToolParam(description = "String email : e-mailul, așa cum îl\n" +
                    "dă  find_customers", required = true) String email) {
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

    @McpTool(name = "get_order",
            description = "o comandă după id, cu aceleași\n" +
                    "câmpuri",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public OrderView getOrder(
            @McpToolParam(description = "UUID orderId : id-ul, așa cum îl dă customer_orders"
                    ,required = false) UUID shipmentId) {
        return OrderView.from(ordersQuerryService.findOrderById(shipmentId));

    }

    @McpTool(name = "find_customers",
            description = "caută după e-mail sau parte din nume;\n" +
                    "întoarce o listă (goală dacă nu\n" +
                    "găsește) cu nume, e-mail, telefon, țară,\n" +
                    "adresă, număr de comenzi",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public List<CustomerView> fimdCustomers(
            @McpToolParam(description = "un e-mail sau o parte\n" +
                    "din numele complet",
            required = true) String name) {
                
             return userQuerryService.searchCustomers(name);

    }

}
