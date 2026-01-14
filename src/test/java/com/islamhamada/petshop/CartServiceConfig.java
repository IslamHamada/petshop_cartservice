package com.islamhamada.petshop;

import com.islamhamada.petshop.controller.CartControllerTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CartServiceConfig {

    @Bean
    public ServiceInstanceListSupplier supplier() {
        return new TestServiceInstanceListSupplier(CartControllerTest.wireMockServer.port());
    }
}
