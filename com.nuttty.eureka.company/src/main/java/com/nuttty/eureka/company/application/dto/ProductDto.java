package com.nuttty.eureka.company.application.dto;

import com.nuttty.eureka.company.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private UUID product_id;
    private UUID company_id;
    private UUID hub_id;

    private String product_name;
    private BigDecimal product_price;
    private Integer product_quantity;

    public static ProductDto toDto(Product savedProduct) {
        return new ProductDto(
                savedProduct.getId(),
                savedProduct.getCompany().getId(),
                savedProduct.getHubId(),
                savedProduct.getProductName(),
                savedProduct.getProductPrice(),
                savedProduct.getProductQuantity()
        );
    }
}
