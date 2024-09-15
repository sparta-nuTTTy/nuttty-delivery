package com.nuttty.eureka.ai.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    private int resultCode;
    private String resultMessage;
    private Page<OrderDto> data;
}
