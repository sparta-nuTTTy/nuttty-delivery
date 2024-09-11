package com.nuttty.eureka.company.application.dto;

import com.nuttty.eureka.company.domain.model.Product;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProductDto {

    private UUID product_id;
    private UUID company_id;
    private UUID hub_id;

    private String product_name;
    private BigDecimal product_price;
    private Integer product_quantity;

    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;

    @QueryProjection
    public ProductDto(UUID product_id, UUID company_id, UUID hub_id, String product_name, BigDecimal product_price, Integer product_quantity, LocalDateTime created_at, String created_by, LocalDateTime updated_at, String updated_by) {
        this.product_id = product_id;
        this.company_id = company_id;
        this.hub_id = hub_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.created_at = created_at;
        this.created_by = created_by;
        this.updated_at = updated_at;
        this.updated_by = updated_by;
    }

    public static ProductDto toDto(Product savedProduct) {
        return new ProductDto(
                savedProduct.getId(),
                savedProduct.getCompany().getId(),
                savedProduct.getHubId(),
                savedProduct.getProductName(),
                savedProduct.getProductPrice(),
                savedProduct.getProductQuantity(),
                savedProduct.getCreatedAt(),
                savedProduct.getCreatedBy(),
                savedProduct.getUpdatedAt(),
                savedProduct.getUpdatedBy()
        );
    }
}
