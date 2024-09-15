package com.nuttty.eureka.ai.application.dto.slackmessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackMessageDto {

    private UUID slackMessageId;
    private UUID deliveryMessageId;
    private String message;
    private LocalDateTime shipmentTime;
}
