package mycode.onlineshopspring.auth.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Override
    @EntityGraph(attributePaths = {"user", "orderSet", "orderSet.orderDetailsSet", "orderSet.orderDetailsSet.product"})
    List<Customer> findAll();

    @EntityGraph(attributePaths = {"user", "orderSet", "orderSet.orderDetailsSet", "orderSet.orderDetailsSet.product"})
    Page<Customer> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderSet"})
    Optional<Customer> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user", "orderSet"})
    Optional<Customer> findByUserEmail(String email);

    @EntityGraph(attributePaths = {"user", "orderSet", "orderSet.orderDetailsSet", "orderSet.orderDetailsSet.product"})
    @Query("""
            select c from Customer c
            where lower(c.fullName) like lower(concat('%', :text, '%'))
               or lower(c.user.email) like lower(concat('%', :text, '%'))
            order by c.fullName
            """)
    List<Customer> search(@Param("text") String text);
}
