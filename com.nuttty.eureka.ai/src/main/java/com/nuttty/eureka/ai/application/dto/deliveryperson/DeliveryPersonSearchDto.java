package com.nuttty.eureka.ai.application.dto.deliveryperson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonSearchDto {

    @JsonProperty("delivery_person_id")
    private Long deliveryPersonId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("hub_id")
    private UUID hubId;

    @JsonProperty("delivery_person_type")
    private String deliveryPersonType;

    @JsonProperty("slack_id")
    private String slackId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("create_by")
    private String createdBy;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("updated_by")
    private String updatedBy;
}
