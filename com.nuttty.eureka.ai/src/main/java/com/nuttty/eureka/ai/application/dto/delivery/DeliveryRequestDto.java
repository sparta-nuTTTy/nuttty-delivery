package com.nuttty.eureka.ai.application.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {

    private int resultCode;
    private String resultMessage;
    private List<DeliveryDto> data;
}
