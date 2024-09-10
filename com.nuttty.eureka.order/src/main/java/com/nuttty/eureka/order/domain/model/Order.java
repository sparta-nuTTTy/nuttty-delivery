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
    private List<OrderProduct> orderProductList = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    public static Order cereate(UUID receiverId, UUID supplierId) {
        return Order.builder()
                .receiverId(receiverId)
                .supplierId(supplierId)
                .totalPrice(BigDecimal.ZERO)
                .build();
    }

    public void addOrderProduct(OrderProduct orderProduct) {
        orderProductList.add(orderProduct);
        totalPrice = totalPrice.add(orderProduct.getTotalPrice());
    }
}

