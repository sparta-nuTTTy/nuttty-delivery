package com.nuttty.eureka.order.domain.repository;

import com.nuttty.eureka.order.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderQueryRepository {
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId AND o.isDelete = false")
    Optional<Order> findByOrderId(UUID orderId);
}
