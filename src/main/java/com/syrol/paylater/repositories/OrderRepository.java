package com.syrol.paylater.repositories;
import com.syrol.paylater.entities.Order;
import com.syrol.paylater.enums.OrderPaymentMethod;
import com.syrol.paylater.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
   Optional<Order> findByOrderReference(String orderReference);
   Optional<List<Order>> findByUuid(String  uuid);
   Optional<Page<Order>> findByStatus(Status status, Pageable pageable);
   Optional<Page<Order>> findByPaymentMethod(OrderPaymentMethod status, Pageable pageable);
}
