package com.nuttty.eureka.ai.application.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiSearchRequestDto {

    private UUID aiId;
    private UUID deliveryManagerId;

    private String question;
    private String answer;

}
