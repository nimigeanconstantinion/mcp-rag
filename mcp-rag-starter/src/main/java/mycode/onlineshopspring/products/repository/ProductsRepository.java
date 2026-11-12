package mycode.onlineshopspring.products.repository;

import mycode.onlineshopspring.products.models.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductsRepository extends JpaRepository<Products, UUID> {

    @Query("""
            select p from Products p
            where lower(p.name) like lower(concat('%', :text, '%'))
               or lower(p.sku) like lower(concat('%', :text, '%'))
               or lower(p.category) like lower(concat('%', :text, '%'))
            order by p.sku
            """)
    List<Products> search(@Param("text") String text);
}
