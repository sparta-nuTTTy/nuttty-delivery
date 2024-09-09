package com.nuttty.eureka.company.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_products")
public class Product extends AuditEntity{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "product_id")
    private UUID id;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "product_name")
    private String productName;

    @Column(precision = 10, scale = 2, name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "product_quantity")
    private Integer productQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
