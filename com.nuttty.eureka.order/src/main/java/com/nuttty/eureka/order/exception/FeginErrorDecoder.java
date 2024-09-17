package com.nuttty.eureka.order.exception;

import com.nuttty.eureka.order.exception.exceptionsdefined.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

/**
 * FeginErrorDecoder
 * Fegin 에러 처리
 */
public class FeginErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (status) {
            case NOT_FOUND -> new ExternalServiceException(HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다.");
            case BAD_REQUEST -> new ExternalServiceException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
            case INTERNAL_SERVER_ERROR ->
                    new ExternalServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다.");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}
