package com.islamhamada.petshop.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.islamhamada.petshop.contracts.exception.FeignClientException;
import com.islamhamada.petshop.contracts.model.RestExceptionResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class FeignClientExceptionDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            RestExceptionResponse restExceptionResponse = objectMapper.readValue(response.body().asInputStream(),
                    RestExceptionResponse.class);
            return new FeignClientException(
                    restExceptionResponse.getError_message(),
                    restExceptionResponse.getError_code(),
                    HttpStatus.resolve(response.status()));
        } catch (IOException e) {
            throw new FeignClientException("Internal Server Error",
                    "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
