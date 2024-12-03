package com.wanted.market.common.exception;

import com.wanted.market.common.dto.ResponseDto;
import com.wanted.market.common.dto.ValidationError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ResponseDto<Void>> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ResponseDto.fail(e.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ResponseDto<List<ValidationError>>> handleBindException(BindException e) {
        log.error("BindException: {}", e.getMessage(), e);
        List<ValidationError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ResponseDto.fail(errors, "입력값이 올바르지 않습니다"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ResponseDto<List<ValidationError>>> handleConstraintViolationException(
            ConstraintViolationException e) {
        log.error("ConstraintViolationException: {}", e.getMessage(), e);
        List<ValidationError> errors = e.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue() == null ? "" : violation.getInvalidValue().toString(),
                        violation.getMessage()))
                .toList();

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ResponseDto.fail(errors, "입력값이 올바르지 않습니다"));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDto<Void>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ResponseDto.fail("예기치 않은 오류가 발생했습니다"));
    }
}
