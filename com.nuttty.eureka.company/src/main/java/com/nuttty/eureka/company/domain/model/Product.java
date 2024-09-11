package com.nuttty.eureka.company.domain.model;

import com.nuttty.eureka.company.exception.exceptionsdefined.NotEnoughStockException;
import com.nuttty.eureka.company.presentation.request.ProductUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_products")
@SQLRestriction("is_delete = false")
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

    public Product(UUID hubId, String productName, BigDecimal productPrice, Integer productQuantity, Company company) {
        this.hubId = hubId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        if (company != null) {
            addCompany(company);
        }
    }

    /**
     * 생성자 메서드
     * @param hubId
     * @param productName
     * @param productPrice
     * @param productQuantity
     * @param company
     * @return
     */
    public static Product createProduct(UUID hubId, String productName, BigDecimal productPrice, Integer productQuantity, Company company) {
        return new Product(hubId, productName, productPrice, productQuantity, company);
    }
    
    public void addCompany(Company company) {
        this.company = company;
        company.getProducts().add(this);
    }

    /**
     * 상품 수정 | 이름, 가격
     * @param request
     * @return
     */
    public Product update(ProductUpdateRequestDto request) {
        this.productName = request.getProduct_name();
        this.productPrice = request.getProduct_price();
        return this;
    }

    /**
     * 수량 추가 메서드
     * @param quantity
     */
    public void addStock(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.productQuantity += quantity;
        } else {
            throw new IllegalArgumentException("Quantity must be greater than 0 or quantity is null");
        }
    }

    /**
     * 수량 감소 메서드
     * @param quantity
     * @return
     */
    public Integer removeStock(Integer quantity) {
        int restStock = this.productQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.productQuantity = restStock;
        return restStock;
    }

    /**
     * 수량 복원 메서드
     * @param quantity
     * @return
     */
    public Integer cancelStock(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0 or quantity is null");
        }else {
            productQuantity = productQuantity + quantity;
        }
        return productQuantity;
    }
}
