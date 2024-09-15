package com.nuttty.eureka.ai.exception.exceptionsdefined;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientException extends RuntimeException {

    private final Integer statusCode;

    public ClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ClientException(String message) {
        super(message);
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
