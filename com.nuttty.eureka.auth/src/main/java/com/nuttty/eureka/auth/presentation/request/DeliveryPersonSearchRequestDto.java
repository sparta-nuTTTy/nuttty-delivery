package com.nuttty.eureka.auth.presentation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryPersonSearchRequestDto {
    private Long delivery_person_id;
    private Long user_id;
    private UUID hub_id;
    private String type;
    private String slack_id;
}
