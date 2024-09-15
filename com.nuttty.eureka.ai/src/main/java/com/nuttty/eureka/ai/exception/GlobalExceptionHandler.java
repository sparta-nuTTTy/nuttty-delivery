package com.nuttty.eureka.ai.exception;

import com.nuttty.eureka.ai.application.dto.ErrorResponse;
import com.nuttty.eureka.ai.exception.exceptionsdefined.ClientException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 유니크 제약 조건 위반 예외
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handlerUnique(DataIntegrityViolationException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Data Integrity Violation",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 엔티티 찾을 수 없음 예외
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNotfound(EntityNotFoundException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Validation 에러 예외
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        fieldError -> fieldError.getField(),
                        Collectors.mapping(
                                fieldError -> fieldError.getDefaultMessage(),
                                Collectors.joining(", ") // 메시지를 ','로 구분하여 하나로 합침
                        )
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errors.toString(),
                request.getDescription(false));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 요청 형식이 틀렸을 경우 예외
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handlerBadRequest(IllegalArgumentException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * JSON 요청 바디의 UUID 변환 실패 예외 처리
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid input format. Please check your request data.",
                ex.getMessage(),
                request.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * OrderClient 예외 정의
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handlerBadRequest(ClientException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode(),
                "Feign Error",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }
}
