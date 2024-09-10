package com.nuttty.eureka.hub.presestation.response;

import com.nuttty.eureka.hub.application.dto.HubDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubResponseDto {

    private int status_code;
    private String result_message;
    private HubDto hubDto;
}
