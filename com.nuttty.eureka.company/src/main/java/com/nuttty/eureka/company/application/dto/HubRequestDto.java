package com.nuttty.eureka.company.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HubRequestDto {

    private int status_code;
    private String result_message;
    private HubDto hubDto;

}
