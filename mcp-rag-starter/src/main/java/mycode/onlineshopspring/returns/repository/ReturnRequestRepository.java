package mycode.onlineshopspring.returns.repository;

import mycode.onlineshopspring.returns.models.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, UUID> {
}
