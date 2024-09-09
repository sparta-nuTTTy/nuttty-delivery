package com.nuttty.eureka.auth.util;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResultResponse<T> implements Serializable {
    // API 응답 결과
    private T data;

    // API 응답 코드
    private int resultCode;

    // API 응답 메시지
    private String resultMessage;

    @Builder
    public ResultResponse(final T data, final int resultCode, final String resultMessage) {
        this.data = data;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    @Builder(builderMethodName = "successBuilder")
    public ResultResponse(final T data, final SuccessCode successCode) {
        this.data = data;
        this.resultCode = successCode.getStatus();
        this.resultMessage = successCode.getMessage();
    }
}