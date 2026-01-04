package com.islamhamada.petshop.external.service;

import com.islamhamada.petshop.contracts.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service-svc",
        path = "/product"
)
public interface ProductService {
    @GetMapping("/{id}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable("id") long id);
}
