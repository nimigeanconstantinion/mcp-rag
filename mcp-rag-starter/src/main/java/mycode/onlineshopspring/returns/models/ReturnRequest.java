package mycode.onlineshopspring.returns.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mycode.onlineshopspring.common.model.AuditableEntity;
import mycode.onlineshopspring.orders.models.Orders;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "return_requests")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = "order")
public class ReturnRequest extends AuditableEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    private String status;
}
