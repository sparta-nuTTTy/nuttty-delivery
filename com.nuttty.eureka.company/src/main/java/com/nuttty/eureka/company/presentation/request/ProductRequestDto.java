package com.nuttty.eureka.company.presentation.request;

import com.nuttty.eureka.company.domain.model.Company;
import com.nuttty.eureka.company.domain.model.Product;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotNull(message = "Company ID must be provided")
    private UUID company_id;

    @NotNull(message = "Hub ID must be provided")
    private UUID hub_id;

    @NotBlank(message = "Product Name must be provided")
    @NotNull(message = "Product Name must be provided")
    private String product_name;

    @Digits(integer = 10, fraction = 2, message = "제품 가격은 총 10자리 숫자까지 입력 가능하며, 소수점 이하 2자리까지 허용됩니다.")
    @NotNull(message = "Product Price must be provided")
    private BigDecimal product_price;

    @NotNull(message = "Product Quantity must be provided")
    @Min(value = 1, message = "Product Quantity must be at least 1")
    private Integer product_quantity;

    public Product toEntity(Company findCompany) {
        return Product.createProduct(hub_id, product_name, product_price, product_quantity, findCompany);
    }
}
