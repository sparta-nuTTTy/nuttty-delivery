package com.nuttty.eureka.company.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HubDto {

    private UUID hub_id;
    private Long user_id;
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String nx;
    private String ny;

    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;
}
