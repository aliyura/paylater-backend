package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.Payment;
import com.syrol.paylater.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReference(String paymentReference);
    Optional<Page<Payment>> findByStatus(Status status, Pageable pageable);
    Optional<List<Payment>> findByUuid(String uuid);
}
