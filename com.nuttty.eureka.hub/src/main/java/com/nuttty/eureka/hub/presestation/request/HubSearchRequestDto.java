package com.nuttty.eureka.hub.presestation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubSearchRequestDto {

    private UUID hub_id;
    private Long user_id;

    private String name;
    private String address;

}
