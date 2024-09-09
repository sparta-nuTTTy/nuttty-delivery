package com.nuttty.eureka.hub.presestation.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubDelResponse {

    private int status_code;
    private String result_message;
    private String result;
}
