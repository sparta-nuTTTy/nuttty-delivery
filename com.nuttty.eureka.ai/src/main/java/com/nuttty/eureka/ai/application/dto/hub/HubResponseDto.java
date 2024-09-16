package com.nuttty.eureka.ai.application.dto.hub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubResponseDto {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("result_message")
    private String resultMessage;

    private HubDto hubDto;
}
