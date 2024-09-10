package com.nuttty.eureka.hub.application.dto;

import com.nuttty.eureka.hub.domain.model.Hub;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubDto {

    private UUID hub_id;
    private Long user_id;
    private String name;
    private String address;
    private String latitude;
    private String longitude;

    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;


    public static HubDto toDto(Hub hub) {
        return new HubDto(hub.getId(),
                hub.getUserId(),
                hub.getName(),
                hub.getAddress(),
                hub.getLatitude(),
                hub.getLongitude(),
                hub.getCreatedAt(),
                hub.getCreatedBy(),
                hub.getUpdatedAt(),
                hub.getUpdatedBy());
    }
}
