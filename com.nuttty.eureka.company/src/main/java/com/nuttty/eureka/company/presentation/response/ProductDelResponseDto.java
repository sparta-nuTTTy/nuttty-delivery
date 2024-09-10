package com.nuttty.eureka.company.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDelResponseDto {

    private int status_code;
    private String result_message;
    private String result;
}
