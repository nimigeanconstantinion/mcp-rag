package mycode.onlineshopspring.mcp.dto;

import mycode.onlineshopspring.products.dto.ProductsResponse;

public record ProductView(
        String sku,
        String name,
        String category,
        int price,
        int stock,
        String description
) {
    public static ProductView from(ProductsResponse product) {
        return new ProductView(product.sku(), product.name(), product.category(), product.price(),
                product.stock(), product.descriptions());
    }
}
