package com.islamhamada.petshop.exception;

import com.islamhamada.petshop.contracts.exception.FeignClientException;
import com.islamhamada.petshop.contracts.model.RestExceptionResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CartServiceException.class)
    public ResponseEntity<RestExceptionResponse> handleCartServiceException(CartServiceException exception){
        log.error(exception);
        return new ResponseEntity<>(RestExceptionResponse.builder()
                .error_message(exception.getMessage())
                .error_code(exception.getError_code())
                .build(), exception.getHttpStatus());
    }

    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<RestExceptionResponse> handleFeignClientException(FeignClientException exception) {
        log.error(exception);
        return new ResponseEntity<>(RestExceptionResponse.builder()
                .error_message(exception.getMessage())
                .error_code(exception.getError_code())
                .build(), exception.getHttpStatus());
    }
}
