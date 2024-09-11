package com.nuttty.eureka.order.domain.model;

import jakarta.persistence.*;
import lombok.*;
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
public class Order extends AuditEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

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
                .build();
    }

    public void addOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts.addAll(orderProducts);
        this.totalPrice = this.orderProducts.stream()
                .map(OrderProduct::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }
}
