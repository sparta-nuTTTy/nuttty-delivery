package com.nuttty.eureka.auth.application.dto;

import com.nuttty.eureka.auth.domain.model.DeliveryPerson;
import com.nuttty.eureka.auth.domain.model.DeliveryPersonTypeEnum;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryPersonInfoDto implements Serializable {
    private Long delivery_person_id;
    private Long user_id;
    private UUID hub_id;
    private DeliveryPersonTypeEnum delivery_person_type;
    private String slack_id;
    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;


    public static DeliveryPersonInfoDto of(DeliveryPerson deliveryPerson) {
        return DeliveryPersonInfoDto.builder()
                .delivery_person_id(deliveryPerson.getDeliveryPersonId())
                .user_id(deliveryPerson.getUserId())
                .hub_id(deliveryPerson.getHubId())
                .delivery_person_type(deliveryPerson.getDeliveryPersonType())
                .slack_id(deliveryPerson.getSlackId())
                .created_at(deliveryPerson.getCreatedAt())
                .created_by(deliveryPerson.getCreatedBy())
                .updated_at(deliveryPerson.getUpdatedAt())
                .updated_by(deliveryPerson.getUpdatedBy())
                .build();
    }


    @QueryProjection
    public DeliveryPersonInfoDto(Long delivery_person_id, Long user_id, UUID hub_id, DeliveryPersonTypeEnum delivery_person_type,
                                 String slack_id, LocalDateTime created_at, String created_by, LocalDateTime updated_at, String updated_by) {
        this.delivery_person_id = delivery_person_id;
        this.user_id = user_id;
        this.hub_id = hub_id;
        this.delivery_person_type = delivery_person_type;
        this.slack_id = slack_id;
        this.created_at = created_at;
        this.created_by = created_by;
        this.updated_at = updated_at;
        this.updated_by = updated_by;
    }
}
