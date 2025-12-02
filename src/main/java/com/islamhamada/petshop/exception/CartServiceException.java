package com.islamhamada.petshop.exception;

import com.islamhamada.petshop.contracts.exception.ServiceException;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CartServiceException extends ServiceException {
    private HttpStatus httpStatus;

    public CartServiceException(String message, String error_code, HttpStatus httpStatus) {
        super(message, "CART_" + error_code);
        this.httpStatus = httpStatus;
    }
}
