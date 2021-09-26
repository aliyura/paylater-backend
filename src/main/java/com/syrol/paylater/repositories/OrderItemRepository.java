package com.syrol.paylater.repositories;

import com.syrol.paylater.entities.Order;
import com.syrol.paylater.entities.OrderItem;
import com.syrol.paylater.enums.OrderPaymentMethod;
import com.syrol.paylater.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<Order> findByOrderId(Long orderId);
}
