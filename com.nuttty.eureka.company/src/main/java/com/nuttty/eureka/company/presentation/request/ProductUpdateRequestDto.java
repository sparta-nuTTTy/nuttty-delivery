package com.nuttty.eureka.company.presentation.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateRequestDto {

    @NotBlank(message = "Product Name must be provided")
    @NotNull(message = "Product Name must be provided")
    private String product_name;

    @Digits(integer = 10, fraction = 2, message = "제품 가격은 총 10자리 숫자까지 입력 가능하며, 소수점 이하 2자리까지 허용됩니다.")
    private BigDecimal product_price;

}
