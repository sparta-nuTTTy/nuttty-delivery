package com.nuttty.eureka.ai.application.dto.deliveryperson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonSearchDto {

    private Long delivery_person_id;
    private Long user_id;
    private UUID hub_id;

    private String delivery_person_type;
    private String slack_id;

    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;
}
