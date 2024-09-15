package com.nuttty.eureka.ai.application.dto.deliveryperson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonRequestDto {

    private int resultCode;
    private String resultMessage;
    private Page<DeliveryPersonDto> data;

}
