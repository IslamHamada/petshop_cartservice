package com.islamhamada.petshop.model;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCartItemRequest {
    @Positive
    private long backend_id;
    @Positive
    private long product_id;
    @Positive
    private int count;
}
