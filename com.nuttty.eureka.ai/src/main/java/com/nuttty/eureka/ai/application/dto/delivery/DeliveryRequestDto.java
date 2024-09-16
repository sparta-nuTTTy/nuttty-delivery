package com.nuttty.eureka.ai.application.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {

    private int resultCode;
    private String resultMessage;
    private Page<DeliveryDto> data;
}
