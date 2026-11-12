package mycode.onlineshopspring.mcp;

import mycode.onlineshopspring.mcp.dtos.ProductView;
import mycode.onlineshopspring.products.service.ProductsQuerryService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShopTools {
    ProductsQuerryService productsQuerryService;

    public ShopTools(ProductsQuerryService productsQuerryService) {
        this.productsQuerryService=productsQuerryService;
    }
    @McpTool(name = "search_products",
            description = "Searches products by text in the name, sku or category (product names are in Romanian). An empty query returns all products. Returns sku, name, category, price in lei, stock and description.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public List<ProductView> searchProducts(
            @McpToolParam(description = "Text to search for, e.g. a sku like LAP-002, a category like Audio, or part of a name", required = false) String query) {
        return productsQuerryService.searchProducts(query).stream().map(ProductView::from).toList();
    }
}