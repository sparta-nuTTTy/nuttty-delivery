package com.nuttty.eureka.company.presentation.response;

import com.nuttty.eureka.company.application.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponseDto {

    private int status_code;
    private String result_message;
    private List<ProductDto> productList;
}
