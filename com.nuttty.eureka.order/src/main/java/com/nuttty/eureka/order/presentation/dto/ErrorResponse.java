package com.nuttty.eureka.order.presentation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status; // HTTP 상태 코드
    private String error; // 에러 코드
    private String message; // 에러 메시지
    private String path; // 에러 발생 경로

    public ErrorResponse(int status, String error, String message, String path) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        this.timestamp = LocalDateTime.parse(LocalDateTime.now().format(formatter));
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}