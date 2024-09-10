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

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public BigDecimal getTotalPrice() {
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public static OrderProduct create(UUID productId, BigDecimal productPrice, Integer quantity) {
        return OrderProduct.builder()
                .productId(productId)
                .productPrice(productPrice)
                .quantity(quantity)
                .build();
    }


}
