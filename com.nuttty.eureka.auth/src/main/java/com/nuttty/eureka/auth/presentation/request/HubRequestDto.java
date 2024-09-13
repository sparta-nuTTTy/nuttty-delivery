package com.nuttty.eureka.auth.presentation.request;

import com.nuttty.eureka.auth.application.dto.HubDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HubRequestDto implements Serializable {
    private int status_code;
    private String result_message;
    private HubDto hubDto;
}
