package com.nuttty.eureka.hub.presestation.response;

import com.nuttty.eureka.hub.application.dto.HubDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubSearchResponseDto implements Serializable {

    private int status_code;
    private String result_message;
    private List<HubDto> hubDto;
}
