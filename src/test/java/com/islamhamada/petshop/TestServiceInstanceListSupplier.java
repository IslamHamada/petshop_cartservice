package com.islamhamada.petshop;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {

    private final int port;

    public TestServiceInstanceListSupplier(int port) {
        this.port = port;
    }

    @Override
    public String getServiceId() {
        return "product-service-svc";
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance> list
                = new ArrayList<>();
        list.add(new DefaultServiceInstance(
                "PRODUCT-SERVICE",
                "product-service-svc",
                "localhost",
                port,
                false
        ));
        return Flux.just(list);
    }
}
