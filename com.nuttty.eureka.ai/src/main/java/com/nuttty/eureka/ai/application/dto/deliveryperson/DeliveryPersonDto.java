package com.nuttty.eureka.ai.application.dto.deliveryperson;

import com.nuttty.eureka.ai.application.dto.ai.AiDto;
import com.nuttty.eureka.ai.application.dto.slackmessage.SlackMessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonDto {

    private UUID deliveryPersonId;
    private UUID userId;
    private UUID hubId;
    private String deliveryPersonType;
    private String slackId;

    private List<AiDto> ai;
    private List<SlackMessageDto> slackMessage;

}
