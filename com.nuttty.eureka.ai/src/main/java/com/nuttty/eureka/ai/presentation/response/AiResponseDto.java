package com.nuttty.eureka.ai.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResponseDto<T> {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("result_message")
    private String resultMessage;

    private T data;
}
