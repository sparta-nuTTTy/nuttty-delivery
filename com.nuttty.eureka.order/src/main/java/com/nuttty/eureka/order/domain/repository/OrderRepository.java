package com.nuttty.eureka.order.domain.repository;

import com.nuttty.eureka.order.domain.model.Order;

public interface OrderRepository {
    Order save(Order order);
}
