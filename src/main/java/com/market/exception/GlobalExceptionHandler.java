package com.market.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FieldErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        FieldErrorResponse.Validation validation = new FieldErrorResponse.Validation();

        for (FieldError fieldError : exception.getFieldErrors()) {
            validation.add(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new FieldErrorResponse(exception.getStatusCode(), exception.getMessage(), validation);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ErrorResponse httpClientErrorException(HttpClientErrorException exception) {
        log.error("httpClientErrorException exception= ", exception);

        return new ErrorResponse(exception.getStatusCode(), exception.getMessage());
    }
}
