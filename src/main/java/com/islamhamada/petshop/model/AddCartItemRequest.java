package com.islamhamada.petshop.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCartItemRequest {
    @PositiveOrZero
    private long backend_id;
    @PositiveOrZero
    private long product_id;
    @Positive
    private int count;
}
