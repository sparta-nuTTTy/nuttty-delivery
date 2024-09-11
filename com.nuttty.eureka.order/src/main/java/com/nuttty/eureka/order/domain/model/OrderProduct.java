package com.nuttty.eureka.order.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_order_products")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct extends AuditEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "order_product_id", updatable = false, nullable = false)
    private UUID orderProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "order_amount", nullable = false)
    private int orderAmount;

    public BigDecimal getTotalPrice() {
        return productPrice.multiply(BigDecimal.valueOf(orderAmount));
    }

    public static OrderProduct createOrderProduct(Order order, UUID productId, BigDecimal productPrice, int orderAmount) {
        return OrderProduct.builder()
                .order(order)
                .productId(productId)
                .productPrice(productPrice)
                .orderAmount(orderAmount)
                .build();
    }
}
