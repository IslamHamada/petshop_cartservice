package com.islamhamada.petshop.config;

import com.islamhamada.petshop.exception.FeignClientExceptionDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientExceptionDecoder();
    }
}
