package com.nuttty.eureka.ai.application.dto.combine;

import com.nuttty.eureka.ai.application.dto.delivery.DeliveryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryWithWeatherDto {

    private List<DeliveryDto> deliveries;
    private String weather;


}
