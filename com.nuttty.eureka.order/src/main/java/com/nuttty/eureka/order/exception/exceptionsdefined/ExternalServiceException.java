package com.nuttty.eureka.order.exception.exceptionsdefined;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends RuntimeException {
    private final HttpStatus status;

    public ExternalServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
