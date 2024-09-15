package com.nuttty.eureka.order.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_orders")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j(topic = "Order")
public class Order extends AuditEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    public static Order createOrder(UUID receiverId, UUID supplierId) {
        return Order.builder()
                .receiverId(receiverId)
                .supplierId(supplierId)
                .orderStatus(OrderStatus.ORDERED)
                .build();
    }

    public void addOrderProducts(List<OrderProduct> orderProducts) {
        log.info("주문 상품을 주문에 추가 : orderProducts = {}", orderProducts.size());
        orderProducts.forEach(orderProduct -> {
            orderProduct.setOrder(this);
            this.orderProducts.add(orderProduct);
        });
        this.totalPrice = this.orderProducts.stream()
                .map(OrderProduct::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void soft(String email) {
        delete(email);

        this.delivery.delete(email);

        List<DeliveryRoute> deliveryRoutes = this.delivery.getDeliveryRoutes();
        for (DeliveryRoute deliveryRoute : deliveryRoutes) {
            deliveryRoute.delete(email);
        }

        for (OrderProduct or : orderProducts) {
            or.delete(email);
        }
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

}
