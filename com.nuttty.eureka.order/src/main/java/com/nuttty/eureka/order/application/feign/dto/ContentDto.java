package com.nuttty.eureka.order.application.feign.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ContentDto {
    private int statusCode;
    private String resultMessage;
    private List<HubDto> hubDto;
}
