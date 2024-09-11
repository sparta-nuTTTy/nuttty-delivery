package com.nuttty.eureka.auth.exception.custom;

public class InvalidAdminPasswordException extends RuntimeException {
    public InvalidAdminPasswordException(String message) {
        super(message);
    }
}
