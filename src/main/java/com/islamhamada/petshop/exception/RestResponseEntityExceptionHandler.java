package com.islamhamada.petshop.exception;

import com.islamhamada.petshop.contracts.model.RestExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CartServiceException.class)
    public ResponseEntity<RestExceptionResponse> handleCartServiceException(CartServiceException exception){
        return new ResponseEntity<>(RestExceptionResponse.builder()
                .error_message(exception.getMessage())
                .error_code(exception.getError_code())
                .build(), exception.getHttpStatus());
    }
}
