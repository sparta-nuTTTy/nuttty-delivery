package com.nuttty.eureka.hub.application.dto;

import com.nuttty.eureka.hub.domain.model.Hub;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
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

    @QueryProjection
    public HubDto(UUID hub_id, Long user_id, String name, String address, String latitude, String longitude, LocalDateTime created_at, String created_by, LocalDateTime updated_at, String updated_by) {
        this.hub_id = hub_id;
        this.user_id = user_id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created_at = created_at;
        this.created_by = created_by;
        this.updated_at = updated_at;
        this.updated_by = updated_by;
    }

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
