package com.nuttty.eureka.company.exception.exceptionsdefined;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException(String message) {
        super(message);
    }
}
