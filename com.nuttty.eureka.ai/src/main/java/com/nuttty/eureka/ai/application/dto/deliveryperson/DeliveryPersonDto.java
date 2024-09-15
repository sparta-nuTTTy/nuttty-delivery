package com.nuttty.eureka.ai.application.dto.deliveryperson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonDto {

    private List<DeliveryPersonSearchDto> deliveryPersonInfoList;

}
