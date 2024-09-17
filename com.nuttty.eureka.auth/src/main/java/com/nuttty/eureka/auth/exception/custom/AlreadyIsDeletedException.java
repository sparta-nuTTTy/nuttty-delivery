package com.nuttty.eureka.auth.exception.custom;

public class AlreadyIsDeletedException extends RuntimeException {
    public AlreadyIsDeletedException(String message) {
        super(message);
    }
}
