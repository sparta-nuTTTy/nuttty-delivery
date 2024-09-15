package com.nuttty.eureka.ai.application.dto.ai;

import com.nuttty.eureka.ai.domain.model.Ai;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AiDto {

    private UUID aiId;
    private UUID deliveryManagerId;
    private String question;
    private String answer;

    public static AiDto toDto(Ai ai) {
        return new AiDto(
                ai.getId(),
                ai.getDeliveryManagerId(),
                ai.getQuestion(),
                ai.getAnswer()
        );
    }

    @QueryProjection
    public AiDto(UUID aiId, UUID deliveryManagerId, String question, String answer) {
        this.aiId = aiId;
        this.deliveryManagerId = deliveryManagerId;
        this.question = question;
        this.answer = answer;
    }
}
