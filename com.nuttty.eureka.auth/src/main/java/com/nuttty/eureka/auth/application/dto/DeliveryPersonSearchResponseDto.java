package com.nuttty.eureka.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonSearchResponseDto implements Serializable {
    List<DeliveryPersonInfoDto> deliveryPersonInfoList;
}
