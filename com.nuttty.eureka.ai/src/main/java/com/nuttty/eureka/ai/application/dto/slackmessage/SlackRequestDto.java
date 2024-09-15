package com.nuttty.eureka.ai.application.dto.slackmessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackRequestDto {

    private String message;
    private UUID deliveryManagerId;
}
