package com.nuttty.eureka.company.presentation.response;

import com.nuttty.eureka.company.application.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private int status_code;
    private String result_message;
    private ProductDto productDto;
}
