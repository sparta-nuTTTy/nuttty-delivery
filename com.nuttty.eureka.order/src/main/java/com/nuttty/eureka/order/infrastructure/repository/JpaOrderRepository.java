package com.nuttty.eureka.order.infrastructure.repository;

import com.nuttty.eureka.order.domain.model.Order;
import com.nuttty.eureka.order.domain.repository.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<Order, Long>, OrderRepository {
}
